import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainApp implements Runnable {

    private Scanner scanner;

    private void startApp() {
        scanner = new Scanner(System.in);
        System.out.println("Wybierz po czym chcesz znaleźć miejsce dla którego wyświetlisz pogodę \n0 - Zakończ działanie \n1 - Nazwa Miasta \n2 - Kod pocztowy \n3 - Pogoda na 16 dni");
        Integer name = scanner.nextInt();
        chooseTypeSearching(name);
    }

    private void chooseTypeSearching(Integer typeNumber) {
        switch (typeNumber) {
            case 0:
                break;
            case 1:
                connectByCityName();
                startApp();
                break;
            case 2:
                connectByZipCode();
                startApp();
                break;
            case 3:
                connectByCityNameForXDays();
                startApp();
                break;
        }
    }

    private void connectByCityNameForXDays() {
        System.out.println("Podaj nazwę miasta: ");
        String cityName = scanner.next();

        System.out.println("Podaj ilość dni dla których chcesz sprawdzić pogodę z przedziału od 1 do 16: ");
        int days = Integer.parseInt(scanner.next());

        while(days < 0 || days > 17) {
            System.out.println("Podaj ilość dni dla których chcesz sprawdzić pogodę z przedziału od 1 do 16: ");
            days = Integer.parseInt(scanner.next());
        }

        try {
            String response = new HttpService().connect(Config.APP_URL_FOR_16DAYS + "?q=" + cityName + ",pl" + "&lang=pl" + "&cnt=" + days + "&appid=" + Config.APP_ID);
            parseJsonForXDays(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectByCityName() {
        System.out.println("Podaj nazwę miasta: ");
        String cityName = scanner.next();
        try {
            String response = new HttpService().connect(Config.APP_URL + "?q=" + cityName + "&appid=" + Config.APP_ID);
            parseJson(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectByZipCode() {
        System.out.println("Podaj kod pocztowy miasta: ");
        String zipcode = scanner.next();
        try {
            String response = new HttpService().connect(Config.APP_URL + "?zip=" + zipcode + ",pl" + "&appid=" + Config.APP_ID);
            parseJson(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseJson(String json) {
        double temp;
        int humidity;
        int pressure;
        int clouds;

        JSONObject rootObject = new JSONObject(json);
        if (rootObject.getInt("cod") == 200) {
            JSONObject mainObject = rootObject.getJSONObject("main");
            DecimalFormat df = new DecimalFormat("#.##");
            temp = mainObject.getDouble("temp");
            temp = temp - 273;

            humidity = mainObject.getInt("humidity");
            pressure = mainObject.getInt("pressure");
            JSONObject cloudsObject = rootObject.getJSONObject("clouds");
            clouds = cloudsObject.getInt("all");

            System.out.println("Temperatura: " + df.format(temp) + " \u00b0C");
            System.out.println("Wilgotność: " + humidity + " %");
            System.out.println("Zachmurzenie: " + clouds + "%");
            System.out.println("Ciśnienie: " + pressure + " hPa");

        } else {
            System.out.println("Error");
        }
    }

    private void parseJsonForXDays(String json) {
        System.out.println(json);
        JSONObject rootObject = new JSONObject(json);

        List<String> tempList = new ArrayList<>();
        List<Double> humidityList = new ArrayList<>();
        List<Integer> cloudsList = new ArrayList<>();
        List<Integer> pressureList = new ArrayList<>();
        List<Double> windList = new ArrayList<>();
        List<String> descriptionList = new ArrayList<>();

        if (rootObject.getInt("cod") != 200) {
            System.out.println("Error");
        } else {
            JSONArray jsonArrayMain = rootObject.getJSONArray("list");
            for(int i= 0; i < jsonArrayMain.length(); i++) {

                JSONObject weatherObject = (JSONObject) jsonArrayMain.get(i);

                DecimalFormat df = new DecimalFormat("#.#");
                double tempDouble = weatherObject.getJSONObject("temp").getDouble("day");
                tempDouble = tempDouble - 273;
                tempList.add(df.format(tempDouble));

                humidityList.add(weatherObject.getDouble("humidity"));
                pressureList.add(weatherObject.getInt("pressure"));
                cloudsList.add(weatherObject.getInt("clouds"));
                windList.add(weatherObject.getDouble("speed"));

                JSONArray weatherDetailsArray = weatherObject.getJSONArray("weather");
                JSONObject weatherDetailsObject = (JSONObject) weatherDetailsArray.get(0);

                descriptionList.add(weatherDetailsObject.getString("description"));

            }

            System.out.println("Wilgotność: " +  humidityList);
            System.out.println("Ciśnienie: " + pressureList);
            System.out.println("Zachmurzenie: " + cloudsList);
            System.out.println("Wiatr" + windList);
            System.out.println("Temperatura: " + tempList);
            System.out.println("Opis: " + descriptionList);

        }
    }

    @Override
    public void run() {
        startApp();
    }
}
