import java.security.MessageDigest;
import java.util.Random;

public class ResetAdminPassword {
    public static void main(String[] args) {
        try {
            String newPassword = "admin123";
            String salt = "b9c0d"; // 使用现有的salt
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            String combined = newPassword + salt;
            byte[] digest = md.digest(combined.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            String hashedPassword = sb.toString();
            
            System.out.println("新管理员密码信息：");
            System.out.println("明文密码: " + newPassword);
            System.out.println("Salt: " + salt);
            System.out.println("加密后密码: " + hashedPassword);
            System.out.println();
            System.out.println("SQL更新语句：");
            System.out.println("UPDATE user SET password = '" + hashedPassword + "' WHERE username = 'admin';");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}