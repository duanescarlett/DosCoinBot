package core.network;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.HashMap;


public class Request {

    private ByteBuffer buffer;
    private URL url;
    private HttpURLConnection con;
    public int responseCode;

    private String host = "cex.io/api/";
    private static final String USER_AGENT = "Mozilla/5.0";

    private static class SingletonHolder {
        private static final Request INSTANCE = new Request();
    }

    public String getHost(){
        return this.host;
    }

    public static Request getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public String[] getArray(String uri) {

        String response = "";
        String[] array = new String[0];

        try {

            URL url = new URL("https://" + this.host + uri);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.connect();

            InputStreamReader in = new InputStreamReader((InputStream) huc.getContent());
            BufferedReader buff = new BufferedReader(in);

            String line = "";


            while (line != null){
                line = buff.readLine();
                if(line != null){
                    //System.out.println(line);

                    array = line.split(",");
                    int i = 0;

                    for (String s:array) {
                        // Remover the chars '[', ']', '"'
                        s = s.replace("[", "");
                        s = s.replace("]", "");
                        s = s.replace("\"", "");
                        array[i] = s;
                        i++;
                    }

                    response += line;
                }
            }

            huc.disconnect();

        }
        catch (Exception e) {
            System.out.println(e);
        }

        return array;

    }

    // HTTP POST request
    public String sendPostAsURL(String POST_PARAMS, String url) throws IOException {

        URL obj = new URL("https://" + this.host + url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("ui.User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        if(!POST_PARAMS.equals("")){
            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(POST_PARAMS);
            wr.flush();
            wr.close();
        }

        this.responseCode = con.getResponseCode();

        try{

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return response.toString();

        }
        catch(FileNotFoundException e){
            System.out.println("File Not Found, Moving on ...");
        }
        catch(IOException e){
            System.out.println("IOException found: " + e);
        }

        return "";

    }

    public String apiCall(String method, String pair, String param, boolean auth, HashMap<String, String> map) {
        return this.post(("https://" + this.host + method + "/" + pair), param, auth, map);
    }

    private String post(String addr, String param, boolean auth, HashMap<String,String> map) {
        boolean sent = false;
        String response = "";

        //System.out.println("API Key: " + map.get("apiKey"));

        while (!sent) {
            sent = true;
            HttpURLConnection connection = null;
            DataOutputStream output = null;
            BufferedReader input = null;
            String charset = "UTF-8";

            try {
                connection = (HttpURLConnection) new URL(addr).openConnection();
                connection.setRequestProperty("User-Agent", "Cex.io Java API");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Charset", charset);

                // Add parameters if included with the call or authorization is required.
                if (param != "" || auth) {
                    String content = "";
                    connection.setDoOutput(true);
                    output = new DataOutputStream(connection.getOutputStream());

                    // Add authorization details if required for the API method.
                    if (auth) {
                        // Generate POST variables and catch errors.
                        String tSig = (String) map.get("signature");
                        String tNon = (String) map.get("nonce");

                        //System.out.println("API Key: " + map.get("apiKey"));
                        content =
                                "key=" + URLEncoder.encode((String) map.get("apiKey"), charset) + "&signature="
                                        + URLEncoder.encode(tSig, charset) + "&nonce="
                                        + URLEncoder.encode(tNon, charset);
                    }

                    // Separate parameters and add them to the request URL.
                    if (param.contains(",")) {
                        String[] temp = param.split(",");

                        for (int a = 0; a < temp.length; a += 2) {
                            content += "&" + temp[a] + "=" + temp[a + 1] + "&";
                        }

                        content = content.substring(0, content.length() - 1);
                    }

                    output.writeBytes(content);
                    output.flush();
                    output.close();
                }
                else {
                    this.responseCode = connection.getResponseCode();

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer res = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response += inputLine;
                    }

                    in.close();
                }

//                String temp = "";
//                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//                while ((temp = input.readLine()) != null) {
//                    response += temp;
//                }

//                input.close();
            } catch (MalformedURLException e) {
                sent = false;
                e.printStackTrace();
            } catch (IOException e) {
                sent = false;
                e.printStackTrace();

                // This will trigger if CloudFlare is active (Cex API is down).
                try {
                    Thread.sleep(50000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

        return response;
    }


}