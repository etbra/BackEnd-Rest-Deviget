import gherkin.deps.com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NasaAPITest {

    @BeforeClass
    public static void suBeforeClass() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        baseURI = "https://api.nasa.gov";
        //Get KEY in: https://api.nasa.gov/index.html#signUp
    }

    @Test
    public void AccessApi() {
        System.out.println("AccessApi");
        String tKey = "HFs06qDVddtuKbod1oM0b5HmvVYH2aJF2PhXvLMj";
        String currentdate = LocalDate.now().toString();

        String myURI1 = "/planetary/apod";
        String date =
                given().
                        params("api_key", tKey).
                        when().
                        get(myURI1).
                        then().
                        statusCode(HttpStatus.SC_OK).
                        body("date", is(currentdate)).
                        extract().jsonPath().get("date");
        System.out.println(date);
    }
    //
    // TestCase: compare: 10 first MARS-PHOTOS of CURIOSITY
    //                    when got photos from a Mars day (SOL=1000 and based on EARTH_DATE)
    //                    the 10 first photos must be the same.
    //                    API_KEY = "HFs06qDVddtuKbod1oM0b5HmvVYH2aJF2PhXvLMj"
    // https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&api_key=DEMO_KEY
    // https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?earth_date=2015-05-30&api_key=DEMO_KEY
    //
    @Test
    public void CheckCuriosityPhotosfromSolesAndEarthDateResultsAreEqual() {
        System.out.println("CheckCuriosityPhotosfromSolesAndEarthDateResultsAreEqual");
        String tKey = "HFs06qDVddtuKbod1oM0b5HmvVYH2aJF2PhXvLMj";
        String solDay = "1000";
        ArrayList<JsonObject> photosSol;
        ArrayList<JsonObject> photosEarthDate;
        String myURI2 = "/mars-photos/api/v1/rovers/curiosity/photos";

        Response response =
                given().
                        params("sol", solDay).
                        params("page", "1"). //for performance purposes
                        params("api_key", tKey).
                        when().
                        get(myURI2).
                        then().
                        statusCode(HttpStatus.SC_OK).
                        extract().response();

        photosSol = response.jsonPath().getJsonObject("photos");
        String earthDate = response.jsonPath().get("photos.earth_date[0]"); //extract: earth_date

        photosEarthDate =
                given().
                        params("earth_date", earthDate).
                        params("api_key", tKey).
                        params("page", "1").
                        when().
                        get(myURI2).
                        then().
                        extract().jsonPath().getJsonObject("photos");

        for (int i = 0; i < 10; i++) {
            assertEquals("Inconsistency in photo:", photosEarthDate.get(i), photosSol.get(i));
        } //Assert fail console output is detailed enough; compare first different photo on that.
    }

    //
    // TestCase: Check number of photos in Manifest record
    //                  gotten TOTAL_PHOTOS for all rovers (Curiosity, Opportunity, Spirit)
    //                  in any day chosen (SOL=1000)
    //                  TOTAL_PHOTOS of Curiosity should be <= 10 * others (Opportunity)
    //                  API_KEY = "HFs06qDVddtuKbod1oM0b5HmvVYH2aJF2PhXvLMj"
    //https://api.nasa.gov/mars-photos/api/v1/manifests/curiosity?api_key=DEMO_KEY
    //
    @Test
    public void ReadManifestToComparePhotoNumPerDateOfRoversCuriosityAndOpportunity() {
        System.out.println("ReadManifestToComparePhotoNumPerDateOfRoversCuriosityAndOpportunity");
        String tKey = "HFs06qDVddtuKbod1oM0b5HmvVYH2aJF2PhXvLMj";
        final String solDay = "1000";
        HashMap<String, Boolean> rovers = new HashMap<>();
        HashMap<String, String> curiositySolPhotos = new HashMap<>();
        HashMap<String, String> opportunitySolPhotos = new HashMap<>();
        HashMap<String, String> spiritSolPhotos = new HashMap<>();
        HashMap<String, String> curiositySolDate = new HashMap<>();
        HashMap<String, String> opportunityDateSol = new HashMap<>();
        HashMap<String, String> spiritDateSol = new HashMap<>();
        List <Integer> soles;
        List <Integer> total;
        List <String> earthDate;

        rovers.put("curiosity", TRUE);
        rovers.put("opportunity", TRUE);
        rovers.put("spirit", FALSE);

        String myURI3 = "/mars-photos/api/v1/manifests/";

        for (String rover : rovers.keySet()){
            if (rovers.get(rover)) {
                Response response;
                response = given().log().all().
                        params("api_key", tKey).
                        when().
                        get(myURI3 + rover).
                        then().
                        statusCode(HttpStatus.SC_OK).
                        contentType(ContentType.JSON).
                        extract().response();
                soles = response.jsonPath().get("photo_manifest.photos.sol");
                total = response.jsonPath().get("photo_manifest.photos.total_photos");
                earthDate = response.jsonPath().get("photo_manifest.photos.earth_date");
                switch (rover) {
                    case "curiosity": {
                        int i = 0;
                        while (i < soles.size()) {
                            curiositySolPhotos.put(Integer.toString(soles.get(i)), Integer.toString(total.get(i)));
                            curiositySolDate.put(Integer.toString(soles.get(i)), earthDate.get(i));
                            i++;
                        }
                        break;
                    }
                    case "opportunity": {
                        int i = 0;
                        while (i < soles.size()) {
                            opportunitySolPhotos.put(Integer.toString(soles.get(i)), Integer.toString(total.get(i)));
                            opportunityDateSol.put(earthDate.get(i), Integer.toString(soles.get(i)));
                            i++;
                        }
                        break;
                    }
                    case "spirit": {
                        int i = 0;
                        while (i < soles.size()) {
                            spiritSolPhotos.put(Integer.toString(soles.get(i)), Integer.toString(total.get(i)));
                            spiritDateSol.put(earthDate.get(i), Integer.toString(soles.get(i)));
                            i++;
                        }
                        break;
                    }
                }
            }
        }
        String photosfromCuri;
        String photosfromOppo;
        String photosfromSpir;
        String datefromCuri;


        if (!curiositySolDate.containsKey(solDay)) {
            System.out.println("Curiosity does not have photos of Sol day (" + solDay + ").");
        } else {
            photosfromCuri = curiositySolPhotos.get(solDay);
            datefromCuri = curiositySolDate.get(solDay);
            System.out.println("The Date of Sol day "+solDay+" is: "+datefromCuri);
            if (rovers.get("opportunity")) {
                if (opportunityDateSol.containsKey(datefromCuri)) {
                    photosfromOppo = opportunitySolPhotos.get(opportunityDateSol.get(datefromCuri));
                    assertTrue("Curiosity ("+ photosfromCuri +" photos) has more than 10 times the photos of Opportunity (" + photosfromOppo + " photos)",
                            Integer.parseInt(photosfromCuri) <= (10 * Integer.parseInt(photosfromOppo)));
                } else {
                    System.out.println("Opportunity does not have photos of day (" + datefromCuri + ").");
                }
            }
            if (rovers.get("spirit")) {
                if (spiritDateSol.containsKey(datefromCuri)) {
                    photosfromSpir = spiritSolPhotos.get(spiritDateSol.get(datefromCuri));
                    assertTrue("Curiosity ("+ photosfromCuri +" photos) has more than 10 times the photos of Spirit (" + photosfromSpir + " photos)",
                            Integer.parseInt(photosfromCuri) <= (10 * Integer.parseInt(photosfromSpir)));
                } else {
                    System.out.println("Spirit does not have photos of day (" + datefromCuri + ").");
                }
            }
        }
    }
    //
    // TestCase: Check number of photos in Manifest record
    //                  gotten TOTAL_PHOTOS for all rovers (Curiosity, Opportunity, Spirit)
    //                  in any day chosen (SOL=1000)
    //                  TOTAL_PHOTOS of Curiosity should be <= 10 * others (Spirit)
    //                  API_KEY = "HFs06qDVddtuKbod1oM0b5HmvVYH2aJF2PhXvLMj"
    //https://api.nasa.gov/mars-photos/api/v1/manifests/curiosity?api_key=DEMO_KEY
    //
    @Test
    public void ReadManifestToComparePhotoNumPerDateOfRoversCuriosityAndSpirit() {
        System.out.println("ReadManifestToComparePhotoNumPerDateOfRoversCuriosityAndSpirit");
        String tKey = "HFs06qDVddtuKbod1oM0b5HmvVYH2aJF2PhXvLMj";
        final String solDay = "1000";
        HashMap<String, Boolean> rovers = new HashMap<>();
        HashMap<String, String> curiositySolPhotos = new HashMap<>();
        HashMap<String, String> opportunitySolPhotos = new HashMap<>();
        HashMap<String, String> spiritSolPhotos = new HashMap<>();
        HashMap<String, String> curiositySolDate = new HashMap<>();
        HashMap<String, String> opportunityDateSol = new HashMap<>();
        HashMap<String, String> spiritDateSol = new HashMap<>();
        List <Integer> soles;
        List <Integer> total;
        List <String> earthDate;

        rovers.put("curiosity", TRUE);
        rovers.put("opportunity", FALSE);
        rovers.put("spirit", TRUE);

        String myURI3 = "/mars-photos/api/v1/manifests/";

        for (String rover : rovers.keySet()){
            if (rovers.get(rover)) {
                Response response;
                response = given().log().all().
                        params("api_key", tKey).
                        when().
                        get(myURI3 + rover).
                        then().
                        statusCode(HttpStatus.SC_OK).
                        contentType(ContentType.JSON).
                        extract().response();
                soles = response.jsonPath().get("photo_manifest.photos.sol");
                total = response.jsonPath().get("photo_manifest.photos.total_photos");
                earthDate = response.jsonPath().get("photo_manifest.photos.earth_date");
                switch (rover) {
                    case "curiosity": {
                        int i = 0;
                        while (i < soles.size()) {
                            curiositySolPhotos.put(Integer.toString(soles.get(i)), Integer.toString(total.get(i)));
                            curiositySolDate.put(Integer.toString(soles.get(i)), earthDate.get(i));
                            i++;
                        }
                        break;
                    }
                    case "opportunity": {
                        int i = 0;
                        while (i < soles.size()) {
                            opportunitySolPhotos.put(Integer.toString(soles.get(i)), Integer.toString(total.get(i)));
                            opportunityDateSol.put(earthDate.get(i), Integer.toString(soles.get(i)));
                            i++;
                        }
                        break;
                    }
                    case "spirit": {
                        int i = 0;
                        while (i < soles.size()) {
                            spiritSolPhotos.put(Integer.toString(soles.get(i)), Integer.toString(total.get(i)));
                            spiritDateSol.put(earthDate.get(i), Integer.toString(soles.get(i)));
                            i++;
                        }
                        break;
                    }
                }
            }
        }
        String photosfromCuri;
        String photosfromOppo;
        String photosfromSpir;
        String datefromCuri;


        if (!curiositySolDate.containsKey(solDay)) {
            System.out.println("Curiosity does not have photos of Sol day (" + solDay + ").");
        } else {
            photosfromCuri = curiositySolPhotos.get(solDay);
            datefromCuri = curiositySolDate.get(solDay);
            System.out.println("The Date of Sol day "+solDay+" is: "+datefromCuri);
            if (rovers.get("opportunity")) {
                if (opportunityDateSol.containsKey(datefromCuri)) {
                    photosfromOppo = opportunitySolPhotos.get(opportunityDateSol.get(datefromCuri));
                    assertTrue("Curiosity ("+ photosfromCuri +" photos) has more than 10 times the photos of Opportunity (" + photosfromOppo + " photos)",
                            Integer.parseInt(photosfromCuri) <= (10 * Integer.parseInt(photosfromOppo)));
                } else {
                    System.out.println("Opportunity does not have photos of day (" + datefromCuri + ").");
                }
            }
            if (rovers.get("spirit")) {
                if (spiritDateSol.containsKey(datefromCuri)) {
                    photosfromSpir = spiritSolPhotos.get(spiritDateSol.get(datefromCuri));
                    assertTrue("Curiosity ("+ photosfromCuri +" photos) has more than 10 times the photos of Spirit (" + photosfromSpir + " photos)",
                            Integer.parseInt(photosfromCuri) <= (10 * Integer.parseInt(photosfromSpir)));
                } else {
                    System.out.println("Spirit does not have photos of day (" + datefromCuri + ").");
                }
            }
        }
    }
}

