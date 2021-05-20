# Starbucks Sales and Card Management System

This was our Academic Project for Database Systems course during 6th semster at IITBBS.

### Contributors:

[K. Jayakar Reddy](https://github.com/jayakar01) and [K. Kishorereddy](https://github.com/kancherlakishorereddy)

## Description
A Database Mangement System to manage the sales and customer cards of a food chain (inspired from Starbucks). Different GUIs are made for Admin, Outlets and Customers each implementing corresponding functionalities mentioned in the [Database Design Document.pdf](https://github.com/kancherlakishorereddy/DBS-Project/blob/master/Design%20Info/Database%20Design%20Document.pdf).

## File Desccription

* [Design Info / Database Design Document.pdf](https://github.com/kancherlakishorereddy/DBS-Project/blob/master/Design%20Info/Database%20Design%20Document.pdf) : <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Design document consisting of the Entities, ER Diagram, Relational Database Tables, Functionalities and Assumptions of this Project.

* [main.sql](https://github.com/kancherlakishorereddy/DBS-Project/blob/master/main.sql) : <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The SQL code for database initialization, PL/SQL Procedures called from JDBC and the Triggers defined on the database.

* [Admin_SCMS](https://github.com/kancherlakishorereddy/DBS-Project/tree/master/Admin_SCMS) : <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Source code of the NetBeans Project for the Java GUI for Admin.

* [Outlet_SCMS](https://github.com/kancherlakishorereddy/DBS-Project/tree/master/Admin_SCMS) : <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Source code of the NetBeans Project for the Java GUI for Outlets.

* [Customer_SCMS](https://github.com/kancherlakishorereddy/DBS-Project/tree/master/Admin_SCMS) : <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Source code of the NetBeans Project for the Java GUI for Customers.

## Note

Techstack used and other things required to run the project on your local machine:

* Database: Oracle Database 11.2
* Editor: Apache Netbeans IDE 12.0 (Java With Ant)
* Libraries and Drivers: JDK 1.8, ojdbc6_g.jar
* Password for Admin_SCMS login: 'admin_scms' without quotes
* Before running the any of the GUI on yor machine 
   - Create the database with all Tables, Procedures and Triggers defined in [main.sql](https://github.com/kancherlakishorereddy/DBS-Project/blob/master/main.sql)
   - Update the connection URL, user and password in the getConnection() call in AdminDB.java, OutletDB.java and CustomerDB.java in the respective Project's source code.
* Customer_SCMS GUI is unfinished and shows unexpexted behaviour

#### This is our first time making a GUI in Java so our choices may not have been the best. You're welcome to check it out and let us know your thoughts.

### Please Star :star:  the repo to show your appreciation. And let me know if  something is not clear about the project.
