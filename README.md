# BackEnd-Rest-Deviget
NOTES:
ZIP file: Configuracao-master

OBJECTIVE:
Create pilot Java test framework for testing NASA's open API.

NASA has an open API: https://api.nasa.gov/index.html#getting-started. 
It grants access to different features e.g: Astronomy Picture of the Day, 
Mars Rover Photos, etc.

It shall test different scenarios that the API offers:

-Retrieve the first 10 Mars photos made by "Curiosity" on 1000 Martian sol.
-Retrieve the first 10 Mars photos made by "Curiosity" on Earth date equal 
to 1000 Martian sol.
-Retrieve and compare the first 10 Mars photos made by "Curiosity" on 1000
sol and on Earth date equal to 1000 Martian sol.
-Validate that the amounts of pictures that each "Curiosity" camera took on
1000 Mars sol is not greater than 10 times the amount taken by other cameras 
on the same date.

IMPLEMENTATION:
PROJECT: Configuracao

Test Class: NasaAPITest.class
	TC1: public void AccessApi()
	TC2: public void CheckCuriosityPhotosfromSolesAndEarthDateResultsAreEqual()
	TC3: public void ReadManifestToComparePhotoNumPerDateOfRovers()

OBS: TC1 do not implemente the required tests


GRADLE:
	https\://services.gradle.org/distributions/gradle-7.1-bin.zip

JAVA:
	Java version "1.8.0_311"
	Java(TM) SE Runtime Environment (build 1.8.0_311-b11)
	Java HotSpot(TM) 64-Bit Server VM (build 25.311-b11, mixed mode)

IDE: IntelliJ
	Build: IC-212.5457.46
    	IntelliJ IDEA, version : 2021.2.3
	BuildNumber: 212.5457.46
	ProductCode: IC


GRADLE DEPENDENCIES INSTALLED:
    testImplementation group: 'junit', name: 'junit', version: '4.12'
    testImplementation group: 'info.cukes', name: 'cucumber-junit', version: '1.2.5'
    implementation group: 'info.cukes', name: 'cucumber-java', version: '1.2.5'
    testImplementation group: 'io.rest-assured', name: 'rest-assured', version: '4.3.3'
    implementation group: 'org.seleniumhq.selenium', name: 'selenium-java', version: '3.141.59'
    testImplementation group: 'org.testng', name: 'testng', version: '7.1.0'
