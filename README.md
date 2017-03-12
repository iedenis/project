# Automatic Reporting System

The project deals with the identification of license plates of vehicles by an open-source software called ALPR, that processes image and takes the license plate number with an accuracy percentage.

The reporting system works through an app that a user installs to his smartphone and can take a photo of the vehicle he suspects to be stolen. 
By clicking on the “send” button, the customer's photo is sent to the server of the reporting system, which job is to save the image in a specific folder. In addition, there is a script that listening the folder, and in case it changes, and new files is added to it (actually the trigger), the system runs ALPR on to the new image saved.

ALPR system decodes the image and takes out the number of license plates. Then it checks the police database. The process will be carried out by parsing the specific page of the police site designated for the purpose of checking stolen cars. And if the detected number is a number is appear to be stolen according the police database, the system makes the complaint to the police in PDF format by using an external Java library with open source itextpdf which contains:

1) The image captured with the application installed on the customer's smartphone.

2) The map locationon  is taken from the metadata that includes the coordinates received through GPS (accurate up to 8 meters) using Google maps API where the picture was taken. 

3) The number identified by the system for verification.

4) Customer information (as an option), such as: name, phone number, e-mail or place of residence in order to contact the customer if necessary. The customer can also remain anonymous.


At the end of the process an email from the reporting system to the police is sent by using an external library with Javax.mail open source.
### Requirements
- The server part can run only on GNU/Linux OS
- The application developed for Android smartphones only
### Installation
The program doesn't require installation. 
1) Clone the repository into your local repository by:
```sh
git clone https://github.com/iedenis/project
```
Go to .jar files folder
```sh
cd jar_files
```
And run the program 
```sh
java -jar server.jar
```
### The project uses the following open source libraries:
| Name | Link |
| ------ | ------ |
| OpenALPR | https://github.com/openalpr/openalpr |
| Itext | https://github.com/itext/itextpdf |
| Java Mail API | https://java.net/projects/javamail/pages/Home |

***
Google static maps API https://developers.google.com/maps/documentation/static-maps/
