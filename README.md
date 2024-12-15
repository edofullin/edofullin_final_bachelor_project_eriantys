# Eriantys 

> [!IMPORTANT]  
> This is **NOT** the repo that was used for development, as that contains copyrighted material by CRANIO that they did not allow us to share. Because of this, trying to compile the project will fail since it misses the required resources. The final deliverable executables where also removed, together with intermediate assignments containing details of other students from other groups.

> [!IMPORTANT]  
> This repo is for consultation only, no more changes will ever me made to the codebase.

> [!NOTE]  
> As bachelor-level courses are in Italian, most documentation and comments in the code are also written in Italian. Sorry.

This repo contains the code for the final project of the bachelor degree in Software Engineering at Politecnico di Milano, academic year 2021/2022. <br/>

### The Team
* [Edoardo Fullin](https://github.com/edofullin)
* [Daniele Gazzola](https://github.com/DanieleGazzola/DanieleGazzola)
* [Giacomo Groppi](https://github.com/GiacomoGroppii)

Teacher: **Pierluigi San Pietro** (Software Engineering)

Final grade for the project: **30L** (100/100 or A+)

## Overview

This applications makes possible to play the board game [https://boardgamegeek.com/boardgame/341286/eriantys]("Eriantys") by Cranio Creations online with friends, and it provides both a text-based interface as well as a GUI written in JavaFX and working on Linux, Windows and MacOS.
In order to play you need to install java JDK version 17 or above

You can find the official rules of the game [here](https://www.craniocreations.it/wp-content/uploads/2021/11/Eriantys_ITA_bassa.pdf), the application mimics almost 1:1 the official rules.

This repository includes:
* initial UML diagram;
* final UML diagram;
* actual Game JAR ( both CLI and GUI version);
* server JAR

Have fun!

### Features 

| Feature               | Status      |
| --------------------- | ----------- |
| Basic Rules           | OK          |
| Network               | OK          |
| Advanced Rules        | OK          |
| All Characters Cards  | OK          |
| CLI                   | OK          |
| GUI                   | OK          |
| Client Disconnection  | OK          |
| Multiple Matches      | OK          |
| Server Persistance    | OK          |
| 4 Players Game        | NOT PRESENT |
| Client Reconnection   | NOT PRESENT |
| Auto Detect Server IP | OK          |

> [!NOTE]  
> Not all features were required to get the maximum grade

### How to play
First you need to start the server, you can either do it from the "start server" button in the GUI or by launching from terminal the server JAR (recommended).
The fist player then needs to start a new game specifying the following options for the match:
* lobby size
* activate expert mode
* activate save game 
* lobby name

The other players will need connect to the match that has been created by connect to the IP address of the computer that runs the server.
The client will attempt to find server on the local network automatically and suggest the server IP. 
The game will automatically start when the lobby size is reached.

### Run instructions
Run the following command from terminal in the directory of the jar files:
- Server: `java -jar eriantys_server.jar` 
- Client CLI: `java -jar eriantys_client_cli.jar` 
- Client CLI: `java -jar eriantys_client_gui.jar`    

Works on MacOS, Linux and Windows.

### Librerie e Plugins

| Libreria/Plugin | Description                                                       |
| --------------- | ----------------------------------------------------------------- |
| Maven           | dependencies management and project build                         |
| Shade           | Maven plugin to generate UberJAR                                  |
| GSON            | converts JSON files into Java Objects (used for network protocol) |
| JavaFX          | Graphic User interface library                                    |
| JUnit           | Unit testing                                                      |
| Jview           | Used in CLI App                                                   |
| Log4j           | logging library for terminal                                      |

### Gameplay Screenshots
![](deliveries/final/img/LoginGUI.png?raw=true)
![](deliveries/final/img/connectionOption.png?raw=true)
![](deliveries/final/img/main2p.png?raw=true)
![](deliveries/final/img/main3p.png?raw=true)
![](deliveries/final/img/Assistant.png?raw=true)
![](deliveries/final/img/Character.png?raw=true)
![](deliveries/final/img/Island.png?raw=true)
![](deliveries/final/img/cli_move_student.png?raw=true)
![](deliveries/final/img/cli_move_mn.png?raw=true)
![](deliveries/final/img/cli_cloud.png?raw=true)