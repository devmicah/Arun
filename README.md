# Arun
(Client Work) A custom parkour pvp style plugin.

Details:
- Teams are setup 1v1, 2v2, 3v3, 4v4. If boolean 'twoPlatforms' false then it adds an extra 2 teams (...for example 1v1v1v1 instead)
- Objective is to break a teams "core block" in order to win the game. Block Tiers and pickaxe ->
  - Cobblestone | Wooden Pickaxe
  - Stone Brick | Stone Pickaxe
  - Iron Block | Iron Pickaxe
  - Obsidian | Diamond Pickaxe
- A kill shop prices and items ideas aren't created yet.
- Parkour platforms generate between 2 (..or more islands). My idea for the platform generation is have 2 points one from the first platform one from the other. More on the generation system below
- Kits setup/gui. Kits will be setup using a certain command, then when a player is in the lobby stage they can select a kit.
- Kits shop/reward system. Players will be awarded with gold ingots by doing certain things in game. For example a kill can be 3 gold ingots, and a win could be 10. Then you can set prices for kits in the shop and players can buy them.

# Systems

Generation System:
![image](https://user-images.githubusercontent.com/56208524/112507516-b51cc000-8d5c-11eb-8ebe-e8d49b829980.png)

Here is an idea I have for the generation system. It calcualtes a line between 2 points set by a user with a "wand" similar to world edit. The 2 points will be saved into a .yml file. P1 and P2 represent the 2 points. The idea is to generate a random distance from the last jump, and a random distance from left to right.

# Videos
Region Management Systen: https://youtu.be/NHdfjIShxfs<br>
Game Configuration System: https://youtu.be/Wm5e0cHLdwM

# Notice
A published jar will not be uploaded as this was originally developed for a client. Open source code is avilable but build configurations will remain hidden.
