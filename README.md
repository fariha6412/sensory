# Sensory

## Tasks done
1. Read sensor values
	- light sensor
	- proximity sensor
	- accelerometer
	- gyroscope 
2. Show values in different cards
3. Record sensor values in sqlite db for every 5 minute
4. Show a notification bar with all sensor values
5. Keep the app running even if the user closes it from task manager

## Challenges faced
1. Value rendering issue
	- RecyclerView had slow rendering problem for that much sampling rate. I applied DiffUtil to solved the slow rendering problem.
   - Turns out while animating the changes in cardView, it gives exception if the app is stopped anytime. So I needed to stop the animation using  android:animateLayoutChanges="false" in the recyclerView.	

2. Resourses closing issue
	- I was getting "resourse failed to closed" error and after researching a while I found out that it was due to subsequent close statement in the sqlite and also I forgot to close the cursor. Though It happens rarely I reduced to amount of opening and closing of the database.

4. Notification wasn't not showing
	- Very requently updating the notification got itself blocked. So reduced the frequency by 1000 milliseconds. 

5. Storage issue
	- I was getting "Eacces (permission denied)" exception when accessing external storage. Turns out that Android 10+ don't give access with only WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE. Had to add 2 more lines.

6. Too much work in main thread
	- I created new threads to solve the problem. Couldn't find any helpful resources on how to increase refresh rate. My bad.

7. Click on adapter items not working
	- Turns out getBindingAdapterPosition() in adapter returns -1. Rapid change in adapter content creates the issue and bindingPosition could not be found. I didn't find any way of solving this yet. That's why I wasn't able to do the time series chart even though I have saved the values for that in the database.

8. Foreground service wasn't working
	- After API 28, forgroundService added an extra notification requirement. For this my application will be showing 2 notifications while running the service. I tried to change the content of that notification getting data via broadcast but didn't work. Still my device stops the service after a while.
  
  
## Lessons learned
1. Service
   - I didn't do anything related to services before, so it was new for me. And in very short time I covered the topic I believe. 
2. Keeping application supervised
   - I now got why applications needs to be updated constantly as the framework gets updated.
3. Complexity
   - While building this applicatioin, I came accross various faults, exceptions and challenges which I had never experienced before. I believe those made me learn lots of new theories.
4. Refresh rate
   - I am still unaware of how it can be updated. Might research on it once again later.
 
### It was fun and frustrating building. Gave almost 24 hours in total(4 days) on this application still this little work has been done. I am ashamed.

