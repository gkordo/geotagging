GeoTag
======

This is the implementation of the CERTH team's project in the Placing task of MediaEval Benchmarking 2013. The project's paper can be found <a href="http://ceur-ws.org/Vol-1043/mediaeval2013_submission_22.pdf">here</a>.

<h2>Main Method</h2>

This is an offline tag-based method, in which a complex geographical-tag model is built from the tags and locations of the images of the training set.  The implemented approach comprises three steps.

A. Filtering: remove noisy and irrelevant tags from the training data, such as machine-tags, and then remove from the training set those images with no tags left.

B. Spatial clustering and local LDA: cluster the training set images based on their location, using k-means on their latitude-longitude values. For each such area that is created, the LDA algorithm is applied to derive a local topic distribution (Local LDAs).

C. Creating bag-of-excluded-words (BoEW): create a set of non-geographic tags, i.e. tags that should not be taken into account for geotagging.

The procedure for the estimation of the location of the query images is the following. 
* Filter the tags of the image that are either machine-tags or belong to the BoEW.
* Compute the Jaccard similarity between this set of tags and the set of tags for each topic of each local LDA.
* Assign the query image to the area with either the highest similarity with any local topic or the highest mean similarity of the topics of each area.
* Determine the most similar training images using Jaccard similarity and use their center-of-gravity.