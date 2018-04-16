# cuckoo-miner-android
A simple android app to explore the performance of running John Tromps cuckoo miner on mobile devices.

The application is based on the SimpleMiner.java by John Tromp.  https://github.com/tromp/cuckoo

What is being done in this app

* Random headers are generated
* Individual thread per header attempts to find a 42 cycle solution
* Number of threads is based on how many cores available.
