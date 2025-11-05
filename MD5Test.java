import java.security.MessageDigest;

public class MD5Test {
    public static void main(String[] args) {
        try {
            // Test user1 password
            String password = "123456";
            String salt = "a1b2c";
            String target = "ce934f2d733561a4f585d5f470701b56";
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            String combined = password + salt;
            byte[] digest = md.digest(combined.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            String result = sb.toString();
            
            System.out.println("Testing user1:");
            System.out.println("Password: " + password);
            System.out.println("Salt: " + salt);
            System.out.println("Combined: " + combined);
            System.out.println("MD5 Hash: " + result);
            System.out.println("Target: " + target);
            System.out.println("Match: " + result.equals(target));
            
            System.out.println("\nTesting admin:");
            // Test admin password
            String adminSalt = "b9c0d";
            String adminTarget = "2580131f40fac24afe7c1e3381a89cf6";
            
            md = MessageDigest.getInstance("MD5");
            combined = password + adminSalt;
            digest = md.digest(combined.getBytes());
            sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            result = sb.toString();
            
            System.out.println("Password: " + password);
            System.out.println("Salt: " + adminSalt);
            System.out.println("Combined: " + combined);
            System.out.println("MD5 Hash: " + result);
            System.out.println("Target: " + adminTarget);
            System.out.println("Match: " + result.equals(adminTarget));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}