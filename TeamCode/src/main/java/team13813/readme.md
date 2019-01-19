# SSFS Robotics 13813 - Autonomous Tutorial
Before you read this instruction, you need to know (or do) the following things. If you don't know how to do then, please watch my video on the Google Drive (Video Tutorial Folder). You should probably speed up the video because there are a lot of interuptions...  
1. Know: How to start the robot's TeleOp Mode
2. Know: How to set up the gamepads
3. Know: How to score in Autonomous Mode (things you need to do to score the maximum points possible)
4. Know: How to drive the robot's TeleOp Mode with no mistakes (don't break the collecting system with the arm)
5. Know: The rules of Autonomous Mode (what you cannot do) <- well this is not that necessary.
6. Do: Watch some actual autonomous modes so that you know the process (at least 3 games that scored well) <- you must do it. You will probably discover some problem problems and strategies like:
 - You sometimes will collide with your teammate: try to avoid the path that your teammate is most likely to take.
 - You will discover strategies like: you can use the wall of the field to adjust the position of the robot when each time the robot end the autonomous mode with a location that is little bit off than you expected.
 - You will discover that you get points in autonomous when your robot can touch - but not completely go into - the crater, unlike how the game-rule-video says.
7. Do: Have the robot 100% prepared (move the center of the weight, put the surgery tube with correct tension, add some surgery tude in the front of the robot so that the robot can move the gold, ext...). Make sure the robot is 18inch by 18in by 18in, if not, remove the white shoe tie so that at least one point it will be within the limit. They only check the dimension when we enter the room which means they may not notice negligible changes in dimension.
8. Do: Have everything else ready: like the team marker...  

# The Structure of the Program
The autonomous mode consists of 4 modes.
 - `WallbreakerKokiAutoNext`
 - `WallfacerKokiAutoNext`
 - `SaveWallbreakerKokiAutoNext`
 - `SaveWallfacerKokiAutoNext`  
 
Don't ask me why they have those strange names. Emmm... May be I should explain it:
 - Everything with `Save` means this is in save mode: it will save whatever you do on your gamepad once you press start.
 - Everything without `Save` would run the operation you saved.
 - Everything with `Wallfacer` is the position facing the crater. (no matter which team you are because they are symmetrical)
 - Everything with `Wallbreaker` is the position facing the home base. (no matter which team you are because they are symmetrical)
 - `KokiAutoNext` just mean it is a Autonomous - not an TeleOp mode. The difference between `Autonomous Mode` and `TeleOp Mode` is whether you have the time limit of 30 second. People generally use `TeleOp Mode` for human control period and `Autonomous Mode` for autonomous period.  

# How the Autonomous Work
Our autonomous work by saving files (that contain all the recorded, and processed gamepad inputs) using two `Save` modes.  
There should be in total 6 files to save according to 2 different locations of the robot and 3 different locations of the minerals.  

## How the program will save the instructions
 1. When you press `init`, the camera will open to detect the first two minerals on the left side of the robot. Because we know that one of the three will be gold, knowing only the left two will make the program to have enough knowledge to know which of the three position the gold is. Please don't let the camera see all of the three minerals because it will likely to break the program. You can see the detection result from the phone on the robot to see if the robot correctly identifies the two minerals.
 2. When you press `start`, the program will record everything you do on both gamepads. After you press start, the debug message should pop out something like `Getting file in {TEAM}{POSITION}{WHERE_GOLD_IS}_LOG.txt; `. The `{TEAM}` should always be `BLUE` because it does not matter. Make sure the `{POSITION}` and the `{WHERE_GOLD_IS}` matches the actual position of your robot (whether it is facing the Crater) and the actual position of the gold mineral.
 3. When you press `stop`, the file will be saved to a path. It will overwrite the file if it exists.
 
## What you should do to set up
 1. Start the robot, but not the program.
 2. Place the Robot in one of the two position (hanging up on the hook).
 3. Place the gold mineral in one of the three position (left, center, right) and the silver minerals in two other positions.
 4. Choose `SaveWallbreakerKokiAutoNext` or `SaveWallfacerKokiAutoNext` (it depends on where you hang the robot)
 5. Set up your gamepad and everything.
 6. Hit init ONCE. Wait a few second for the program to initiate.
 7. Two people stand in position like an actual game.
 8. When you are ready, press start. There will be a timer of 30 seconds after you press start.
 9. Drive the robot to score point using the most conservative way. After you remove the gold and deposit the team marker, go to the opponent's crater if you are a `Wallbreaker` and go to alliance's crater if you are a `Wallfacer`. This ensures that two robot does not hit each other in autonomous period. For more details and things to take care of, watch more good and bad gameplays to see how each team succeed or failed.
 10. Stop the mode if everything that need to be done is finished in 30s. This step is not necessary because you have 30s limit. But without this step may cause the phone to generate some errors. Although the errors goes away after less than 30s or restart the program, it is not safe...
 11. Test the autonomous program with exact setting when you start the save mode. (minerals in place, team marker in place, hanging)
 12. If everything works fine and the robot scored everything slowly but surely. Good! You set up the first Autonomous mode, 5 to go.
 13. Repeat above another 3-1=2 times but with different position of gold mineral.
 14. Repeat above another 2-1=1 time but with different position of the robot.
 15. You should have in total save 6 different operations in 2 modes.

There may be some grammar mistakes, but don't be picky. :)  
If you have any question, text me and WeChat me.