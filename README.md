# Weather Info Manager

## Description

A student project for "Databases II" course of department of Informatics - Ionian University.

Weather Info Manager is a desktop application developed using Java and Swing GUI API that displays dump weather data recorded by various meteorological stations across USA. The app imports these data from CSV files into a MySQL database and executes queries depending on user's requests. The data is stored into three tables, "MET_STATION", "TEMPERATURE" and "WIND_INFO", as shown bellow.

**MET_STATION**
| ID | NAME | STATE | LATITUDE | LOGITUDE | ALTITUDE |
| -- | ---- | ----- | -------- | -------- | -------- |

**TEMPERATURE**
| MET_STATION | MONTH | YEAR | MAX_TEMP | MIN_TEMP |
| ----------- | ----- | ---- | -------- | -------- |

**WIND_INFO**
| MET_STATION | WIND_DIR | YEAR | NO_OF_DAYS |
| ----------- | -------- | ---- | ---------- |

The first table contains the names of the meteorological stations, their location (state) and their coordinates (latitude, longitude, altitude), second table stores the maximum and minimum temperatures that recorded each month and year at each station and third table includes information about the winds that were recorded by each station (year and duration in days).

Java Database Connectivity (JDBC) API used in order to access the MySQL database server.

## Features

Users are able to:
- connect to the database server,
- create/delete the tables in the database and insert data into them and
- execute various queries such as:
  - get information about a meteorological station by selecting one through a list,
  - retrieve all stations that recorded the maximum and the minimum temperature of a specific year by determining the year,
  - display the average of maximum and minimum temperatures of a station by selecting one through a list,
  - get information about the winds recorded by each station on a specific year by defining that year and
  - get information about the longest-lasted winds recorded by each station.

The application also displays error messages when connection with the database has failed, tables do not exist and data insertion or query execution is requested, tables do not contain any data, invalid input values inserted, etc.

## GUI

Application displays a window (JFrame) that contains a panel (JPanel) with Card Layout. This panel contains six other panels (cards) which become visible one at the time and contain various components (JLabel, JTextField, JButton, etc.) for the main menu and the queries. Also, app displays pop-up message dialogs (JOptionPane) in order to inform users about errors or the progress of their requests. Finally, system's look and fell is being used to inherit system's style.

|                             |                             |
| :-------------------------: | :-------------------------: |
| ![Connect Dialog](https://github.com/p17griv/weather-info-manager/blob/main/img/connect-dialog.png) | ![Main Menu](https://github.com/p17griv/weather-info-manager/blob/main/img/main-menu.png) |
| ![Query 1](https://github.com/p17griv/weather-info-manager/blob/main/img/query1.png) | ![Query 2](https://github.com/p17griv/weather-info-manager/blob/main/img/query2.png) |

## Installation

Download the [latest release](https://github.com/p17griv/weather-info-manager/releases/tag/v1.0.0) of the executable and follow the provided instructions.

## License

See the [LICENSE](https://github.com/p17griv/weather-info-manager/blob/main/LICENSE) file for license rights and limitations (MIT).
