# Please set the project home directory        
project_dir=/home/vagrant/JXCascading-detector          #JX - NO "/" at the end


bug_id=hd-5153
dm_dir=/tmp/${bug_id}_dm    #like "/tmp/mr-4576_dm"



# Start hadoop to clean up data
echo "JX - INFO - clean up HDFS data (need ensure started)"
start-dfs.sh
sleep 5s
#hadoop dfsadmin -safemode leave
hdfs dfsadmin -safemode leave
sleep 2s
#hadoop fs -rmr input
hdfs dfs -rm -r /user/vagrant/input
sleep 1s
#hadoop fs -mkdir /user/vagrant/input
hdfs dfs -mkdir /user/vagrant/input
sleep 1s
#hadoop fs -lsr /
hdfs dfs -ls -R -h /
sleep 5s

# Stop hadoop so that NOT under logging
echo "JX - INFO - stop hadoop .."
stop-dfs.sh
if [ $? -ne 0 ]; then
  echo "stop-all.sh error"
  exit
fi
sleep 3s
rm ~/hadoop/install/hadoop-2.3.0/logs/*


# Clean
echo "JX - INFO - clean $dm_dir .."
if [ -d $dm_dir ]; then
  rm -rf $dm_dir
fi
mkdir $dm_dir


# Compile
echo "JX - INFO - compile jar-dm-$bug_id .."
cd $project_dir
ant jar-dm-$bug_id
if [ $? -ne 0 ]; then
  echo "compile error"
  exit
fi


