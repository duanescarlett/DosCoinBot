package core.modules;

import java.util.HashMap;

public class DataStacks {

    private class Pairs{

        String[] pairList = {
                "ETH/USD",
                "BCH/USD",
                "DASH/USD",
                "ZEC/USD",
                "BTC/EUR",
                "ETH/EUR",
                "BCH/EUR",
                "DASH/EUR",
                "ZEC/EUR",
                "BTC/RUB",
                "ETH/BTC",
                "BCH/BTC",
                "DASH/BTC",
                "ZEC/BTC",
                "GHS/BTC"
        };

        public String[] getList(){
            return this.pairList;
        }

    }

    public static class AltCoins {

        public static String[] coinList = {
                "ETH",
                "BCH",
                "DASH",
                "ZEC"
        };

        public static String[] getCoinList() {
            return coinList;
        }

    }

    public static class BaseCoins {

        public static String[] coinList = {
                "USD",
                "EUR",
                "GBP",
                "BTC"
        };

        public static String[] getCoinList() {
            return coinList;
        }

    }

    public static class MinPurchase {

        private static HashMap coinAmt(){
            HashMap<String,Double> amt = new HashMap<>();

            amt.put("ETH", 0.10000000);
            amt.put("BCH", 0.10000000);
            amt.put("DASH", 0.01000000);
            amt.put("ZEC", 0.01000000);

            return amt;
        }

        public static HashMap getPurchaseUnit(){
            return coinAmt();
        }
    }

}
