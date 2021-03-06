#!/usr/bin/env python

import numpy as np
import sklearn
from sklearn.preprocessing import LabelEncoder


import pickle

from sensor_stick.srv import GetNormals
from sensor_stick.features import compute_color_histograms
from sensor_stick.features import compute_normal_histograms
from visualization_msgs.msg import Marker

from sensor_stick.marker_tools import *
from sensor_stick.msg import DetectedObjectsArray
from sensor_stick.msg import DetectedObject
from sensor_stick.pcl_helper import *

def get_normals(cloud):
    get_normals_prox = rospy.ServiceProxy('/feature_extractor/get_normals', GetNormals)
    return get_normals_prox(cloud).cluster

# Callback function for your Point Cloud Subscriber
def pcl_callback(pcl_msg):

# Exercise-2 TODOs:

    # TODO: Convert ROS msg to PCL data
    pcl_data = ros_to_pcl(pcl_msg)
    vox = pcl_data.make_voxel_grid_filter()

    # TODO: Voxel Grid Downsampling
    LEAF_SIZE = .01
    vox.set_leaf_size(LEAF_SIZE, LEAF_SIZE, LEAF_SIZE)
    down_sampled_pcl = vox.filter()

    # TODO: PassThrough Filter
    passthrough = down_sampled_pcl.make_passthrough_filter()
    filter_axis = 'z'
    passthrough.set_filter_field_name(filter_axis)
    axis_min = 0.6
    axis_max = 1.1
    passthrough.set_filter_limits(axis_min,axis_max)
    passthrough_pcl = passthrough.filter()

    # TODO: RANSAC Plane Segmentation
    seg = passthrough_pcl.make_segmenter()
    seg.set_model_type(pcl.SACMODEL_PLANE)
    seg.set_method_type(pcl.SAC_RANSAC)

    # TODO: Extract inliers and outlier
    seg_max_distance = .01
    seg.set_distance_threshold(seg_max_distance)
    inliers, coefficients = seg.segment()
    table_data = passthrough_pcl.extract(inliers, negative=False)
    object_data = passthrough_pcl.extract(inliers, negative=True)

    # TODO: Euclidean Clustering
    white_cloud = XYZRGB_to_XYZ(object_data)
    tree = white_cloud.make_kdtree()

    ec = white_cloud.make_EuclideanClusterExtraction()
    ec.set_ClusterTolerance(0.02)
    ec.set_MinClusterSize(30)
    ec.set_MaxClusterSize(2000)
    ec.set_SearchMethod(tree)
    cluster_indices = ec.Extract()
    cluster_color = get_color_list(len(cluster_indices))
    color_cluster_point_list = []

    detected_objects_labels = []
    detected_objects = []
    for j, indices in enumerate(cluster_indices):
        
        # Grab the points for the cluster from the extracted outliers
        pcl_cluster = object_data.extract(indices)

        # convert the cluster from pcl to ROS using helper
        # function
        ros_cluster = pcl_to_ros(pcl_cluster)

        # Extract histogram features per capture_features.py (not features.py)
        chists = compute_color_histograms(ros_cluster, using_hsv = False)
        normals = get_normals(ros_cluster)
        nhists = compute_normal_histograms(normals)
        feature = np.concatenate((chists, nhists))

        # Make the prediction, retrieve the label for the result
        # and add it to the detected_objects_labels list
        prediction = clf.predict(scaler.transform(feature.reshape(1,-1)))
        label = encoder.inverse_transform(prediction)[0]
        detected_objects_labels.append(label)

        # Publish a label into RViz
        label_pos = list(white_cloud[indices[0]])
        label_pos[2] += .4
        print("This is the label we've created :", label)
        object_markers_pub.publish(make_label(label, label_pos, j))

        # Add the detected object to the list of detected objects
        do = DetectedObject()
        do.label = label
        do.cloud = ros_cluster
        detected_objects.append(do)
        
        for i, indice in enumerate(indices):
            color_cluster_point_list.append([white_cloud[indice][0],
                                            white_cloud[indice][1],
                                            white_cloud[indice][2],
                                            rgb_to_float(cluster_color[j])])
            
    rospy.loginfo('Detected {} objects: {}'.format(len(detected_objects_labels),detected_objects_labels))
    

    cluster_cloud = pcl.PointCloud_PointXYZRGB()
    cluster_cloud.from_list(color_cluster_point_list)
    ros_cluster_cloud = pcl_to_ros(cluster_cloud)

    # TODO: Create Cluster-Mask Point Cloud to visualize each cluster separately

    # TODO: Convert PCL data to ROS messages
    ros_cloud_objects = pcl_to_ros(object_data)
    ros_cloud_table = pcl_to_ros(table_data)

    # TODO: Publish ROS messages
    pcl_objects_pub.publish(ros_cloud_objects)
    pcl_table_pub.publish(ros_cloud_table)
    pcl_cluster_pub.publish(ros_cluster_cloud)
    detected_objects_pub.publish(detected_objects)

# Exercise-3 TODOs: 

    # Classify the clusters! (loop through each detected cluster one at a time)


        # Grab the points for the cluster

        # Compute the associated feature vector

        # Make the prediction

        # Publish a label into RViz

        # Add the detected object to the list of detected objects.

    # Publish the list of detected objects

if __name__ == '__main__':

    # TODO: ROS node initialization
    rospy.init_node('clustering', anonymous=True)

    # TODO: Create Subscribers
    pcl_sub = rospy.Subscriber("/sensor_stick/point_cloud",pc2.PointCloud2, pcl_callback, queue_size=1)

    # TODO: Create Publishers
    pcl_objects_pub = rospy.Publisher("/pcl_objects", PointCloud2, queue_size=1)
    pcl_table_pub = rospy.Publisher("/pcl_table",PointCloud2, queue_size=1)
    pcl_cluster_pub = rospy.Publisher("/pcl_cluster",PointCloud2, queue_size=1)
    object_markers_pub = rospy.Publisher("/object_markers",Marker, queue_size=1)
    detected_objects_pub = rospy.Publisher("/detected_objects",DetectedObjectsArray, queue_size=1)
    

    # TODO: Load Model From disk
    model = pickle.load(open('model.sav','rb'))
    print("These are the model keys :", model.keys())
    clf = model['classifier']
    encoder = LabelEncoder()
    encoder.classes_ = model['classes']
    scaler = model['scaler']
    
    # Initialize color_list
    get_color_list.color_list = []

    # TODO: Spin while node is not shutdown
    while not rospy.is_shutdown():
        rospy.spin()
