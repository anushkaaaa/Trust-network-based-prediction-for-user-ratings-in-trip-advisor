import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

public class calculateError {
    private static HashMap<String, Double[]> AuthorGlobalValues = new HashMap<>();
    private static HashMap<String, Double[]> HotelNewRating = new HashMap<>();
    private static Object authorInfo = null;
    private static Object hotelsInfo = null;
    private static Object reviews = null;
    private static DecimalFormat df2 = new DecimalFormat(".##");
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        try {
            // classified dataset
            authorInfo = parser.parse(new FileReader(("dataset\\classified dataset\\AuthorInfo.json")));
            hotelsInfo = parser.parse(new FileReader(("dataset\\classified dataset\\hotels_Info.json")));
            reviews = parser.parse(new FileReader(("dataset\\classified dataset\\Reviews.json")));
            // random dataset
//            authorInfo = parser.parse(new FileReader(("dataset\\random dataset\\AuthorInfo.json")));
//            hotelsInfo = parser.parse(new FileReader(("dataset\\random dataset\\hotels_Info.json")));
//            reviews = parser.parse(new FileReader(("dataset\\random dataset\\Reviews.json")));

            calculateGlobalValues();
            calculateNewRating();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void calculateGlobalValues() {
        JSONObject obj = (JSONObject) authorInfo;
        Double[] globalValue = new Double[2];
        JSONArray array = (JSONArray) obj.get("AuthorInfo");
        System.out.println("AuthorId     Global m      Global c");
        for (Object o : array) {
            JSONObject Author = (JSONObject) o;
            String id = Author.get("Author").toString();
            JSONArray r = (JSONArray) Author.get("AuthorReviewerList");
            JSONArray ar = (JSONArray) Author.get("AuthorRating");
            JSONArray m = (JSONArray) Author.get("Interactions");
            JSONArray con = (JSONArray) Author.get("Variance");
            String[] reviewerList = new String[3];
            Double[] authorRating = new Double[3];
            Double[] interaction = new Double[3];
            Double[] variance = new Double[3];
            int i = 0;
            for (Object c : r) {
                reviewerList[i] = (String) c;
                i++;
            }
            i = 0;
            for (Object d : ar) {
                authorRating[i] = (Double) d;
                i++;
            }
            i = 0;
            for (Object e : m) {
                interaction[i] = (Double) e;
                i++;
            }
            i = 0;
            for (Object f : con) {
                variance[i] = (Double) f;
                i++;
            }
            Double a1 = 0.0;
            Double a2 = 0.0;
            Double a3 = 0.0;
            for (int j = 0; j < 3; j++) {
                a1 += authorRating[j] / Math.pow(variance[j], 2);
                a2 += 1 / Math.pow(variance[j], 2);
                a3 += Math.sqrt(Math.pow(variance[j], 2) / interaction[j]);
            }
            globalValue[0] = a1 / a2;
            globalValue[1] = a3;
            System.out.println(id+"     "+df2.format(globalValue[0])+"           "+df2.format(globalValue[1]));
            AuthorGlobalValues.put(id,globalValue);
        }
    }
    private static void calculateNewRating() {
        JSONObject obj1 = (JSONObject) hotelsInfo;
        Double num = 0.0;
        Double deno = 0.0;
        JSONArray hArray = (JSONArray) obj1.get("Hotel");
        Double[] globalValue;
        System.out.println("\nHotelId     oldRating      newRating");
        for (Object h : hArray) {
            JSONObject hotel = (JSONObject) h;
            Double oldRating = (Double) hotel.get("Rating");
            String id = hotel.get("HotelID").toString();
            int n = Integer.parseInt(hotel.get("HotelRatingCount").toString());
            JSONArray r = (JSONArray) hotel.get("AuthorList");
            String[] AuthorList = new String[n];
            int j = 0;
            for (Object d : r) {
                AuthorList[j] = d.toString();
                j++;
            }
            JSONObject obj2 = (JSONObject) authorInfo;
            JSONArray aArray = (JSONArray) obj2.get("AuthorInfo");
            JSONObject obj3 = (JSONObject) reviews;
            JSONArray rArray = (JSONArray) obj3.get("Reviews");
                for(Object a : aArray){
                    JSONObject author = (JSONObject) a;
                    for(int i=0; i<AuthorList.length; i++) {
                        if (author.get("Author").equals(AuthorList[i])) {
                            globalValue = AuthorGlobalValues.get(author.get("Author"));
                            for(Object re :rArray ){
                                JSONObject review = (JSONObject) re;
                                if(review.get("Author").toString().equalsIgnoreCase(AuthorList[i])){
                                    num += globalValue[0]* globalValue[1]* (double) review.get("Ratings");
                                    deno += globalValue[0]* globalValue[1];
                                    break;
                                }
                            }
                        }
                    }
                }
            Double newRating = num/deno;
                Double[] ratings = new Double[2];
                ratings[0] = oldRating;
                ratings[1] = newRating;
            HotelNewRating.put(id,ratings);
            System.out.println(id+"     "+oldRating+"           "+df2.format(newRating));
        }
    }

}

