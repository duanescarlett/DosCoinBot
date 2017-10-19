package core;

import core.modules.DataStacks;
import core.modules.FileWrapper;
import core.network.Request;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.crypto.Data;
import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Main main;
    private Request request;
    private int nonce;
    private final String apiKey;
    private final String userID;
    private final String secret;
    private FileWrapper fileWrapper;
    @FXML
    private Button goButton;

    public Controller() throws IOException {
        //this.main = new Main();
        this.apiKey = "pff2Pdlkhudhjgdfasgfdj994";
        this.userID = "khgugf";
        this.secret = "oiyt7uy5et5rewstyt7yeyt";
        this.nonce = Integer.valueOf((int) (System.currentTimeMillis() / 1000));
        this.goButton = new Button();
        this.request = Request.getInstance();
        this.fileWrapper = new FileWrapper();
    }

    public void setMain(Main main){
        this.main = main;
    }

    @FXML
    public void tradeBtnOnClick() throws IOException, InvalidKeyException, NoSuchAlgorithmException {

        int count = 1;

        while (1 >= count) {

            this.hunter();
            count++;

        }

    }

    // Set up authentication params
    private HashMap<String, String> auth(){
        int nonce;
        HashMap<String, String> pass = new HashMap<>();
        nonce = this.nonce + 1;
        pass.put("nonce", String.valueOf(nonce));
        pass.put("signature", this.signature());
        pass.put("apiKey", this.apiKey);
        return pass;
    }

    private HashMap hunter() throws IOException {

        String[] coinList = DataStacks.AltCoins.getCoinList();
        HashMap coinAmt = DataStacks.MinPurchase.getPurchaseUnit();
        String[] baseCoins = DataStacks.BaseCoins.getCoinList();

        double procedureSpent;
        double procedureGained;
        double procedureFinal;

        // These maps are for organizing the data
        HashMap<String, Double> baseCoin = new HashMap<>();
        HashMap<String, Double> altCoin = new HashMap<>();
        HashMap<String, Double> bitCoin = new HashMap<>();

        for (double i = 0; i < 5; i++) {
            for (String symbol : coinList) {

                for (String coin: baseCoins) {

                    // Get amount and calculate the price in BTC
                    JSONObject altCoinBtc = this.lastPrice(symbol + "/BTC");
                    double altCoinBtcPrice = Double.parseDouble(String.valueOf(altCoinBtc.get("lprice")));
                    System.out.println("Altcoin : BTC price: " + altCoinBtcPrice);
                    // Put the data in the map
                    altCoin.put("Trade Price", ((double) coinAmt.get(symbol) * i) * altCoinBtcPrice);
                    // Take the trade cost and calculate the fee
                    altCoin.put("Trade fee", altCoin.get("Trade Price") * 0.02);

                    System.out.println("Investigation: " + (double) coinAmt.get(symbol));
                    System.out.println("Trade Price: " + (double) altCoin.get("Trade Price"));
                    System.out.println("Trade Fee: " + (double) altCoin.get("Trade fee"));

                    // Take the full amount of alt coin and sell it for a base coin
                    JSONObject baseCoinBtc = this.lastPrice(symbol + "/" + coin);
                    double baseCoinBtcPrice = Double.parseDouble(String.valueOf(baseCoinBtc.get("lprice")));
                    // Calculate the trade price and put the data in the map
                    baseCoin.put("Trade Price", (Double.parseDouble(coinAmt.get(symbol).toString()) * i) * baseCoinBtcPrice);
                    // Take the trade cost and calculate the fee
                    baseCoin.put("Trade fee", baseCoin.get("Trade Price") * 0.02);
                    System.out.println("Trade Price: " + baseCoin.get("Trade Price"));
                    System.out.println("Trade Fee: " + baseCoin.get("Trade fee"));

                    // Use the base coin or fiat to buy btc
                    JSONObject bctFiatLastprice = this.lastPrice("BTC/" + coin);
                    double btcFiatPrice = Double.parseDouble(String.valueOf(bctFiatLastprice.get("lprice")));
                    // Calculate the trade price and put the data in the map
                    bitCoin.put("Trade Price", (Double.parseDouble(coinAmt.get(symbol).toString()) * i) * btcFiatPrice);
                    // Take the trade cost and calculate the fee
                    bitCoin.put("Trade fee", bitCoin.get("Trade Price") * 0.02);
                    System.out.println("Trade Price: " + bitCoin.get("Trade Price"));
                    System.out.println("Trade Fee: " + bitCoin.get("Trade fee"));

                    // Procedure Spent
                    procedureSpent = altCoin.get("Trade Fee") + baseCoin.get("Trade Fee") + bitCoin.get("Trade Fee");

                    // Procedure Gained
                    procedureGained = bitCoin.get("Trade Price") - altCoin.get("Trade Price");

                    // Procedure Final
                    procedureFinal = procedureGained - procedureSpent;
                    System.out.println(procedureFinal);


                    if(procedureFinal > 0){
    //                        // 1. Use bitcoin to buy altcoin
    //                        this.instantBuy(symbol, symbol + "/BTC");
    //
    //                        // 2. Use altcoin to buy USD
    //                        this.instantBuy(symbol, symbol + "/USD");
    //
    //                        // 3. Use USD to buy bitcoin
    //                        this.instantBuy(symbol,"BTC/USD");
                    }

                    // Slow down the request
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }




                this.fileWrapper.close();


            }
        }
        return new HashMap();

    }

    private void dolarCostAvg(){

    }

    private JSONObject lastPrice(String pair) throws IOException {
        String s = this.request.apiCall("last_price", pair,"",false, this.auth());
        return this.json(s);
    }

    private JSONObject altUsd(String coin) throws IOException {
        String s = this.request.apiCall("last_price", coin + "/USD", "", false, this.auth());
        return this.json(s);
    }

    private JSONObject altEur(String coin) throws IOException {
        String s = this.request.apiCall("last_price", coin + "/USD", "", false, this.auth());
        return this.json(s);
    }

    private JSONObject altGbp(String coin) throws IOException {
        String s = this.request.apiCall("last_price", coin + "/USD", "", false, this.auth());
        return this.json(s);
    }

    private void instantBuy(String symbol, String pair){
        HashMap coinAmt = DataStacks.MinPurchase.getPurchaseUnit();
        double amt = (double) coinAmt.get(symbol);
        String s = this.request.apiCall("place_order", pair, ("type,buy,amount," + coinAmt.get(symbol) + ",order_type,market"), true, this.auth());
        this.json(s);

        JSONObject result = this.json(s);
        System.out.println(result.get("message"));

    }

    // Create a clean Json object
    private JSONObject json(String s){
        s = s.replace("[", "").replace("]", "").trim();
        JSONObject obj = new JSONObject(s);
        return obj;
    }

    private String signature() {
        ++this.nonce;
        String message = new String(this.nonce + this.userID + this.apiKey);
        Mac hmac = null;

        try {
            hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key =
                    new SecretKeySpec(((String) this.secret).getBytes("UTF-8"), "HmacSHA256");
            hmac.init(secret_key);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return String.format("%X", new BigInteger(1, hmac.doFinal(message.getBytes())));
    }

    /**
     * Debug the contents of the a CexAPI Object.
     *
     * @return The CexAPI object data: username, apiKey, apiSecret, and nonce.
     */
    public String toString() {
        return "{\"username\":\"" + this.userID + "\",\"apiKey\":\"" + this.apiKey
                + "\",\"apiSecret\":\"" + this.secret + "\",\"nonce\":\"" + this.nonce + "\"}";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
