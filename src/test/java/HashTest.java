
import java.util.Arrays;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class HashTest {

    public static void main(String[] args) {
        String a = "suss111amogus8645";
        String b = "sus";

        String c = "iCOMIT!";
        String d = "iCOMIT!91";

        System.out.println(noNegatives(a.hashCode()));
        System.out.println("Str length " + stringLength(a));

        System.out.println(b.hashCode());
        System.out.println("Hash length " + hashLength(b.hashCode()));
        System.out.println("Str length " + stringLength(b));

        System.out.println(c.hashCode());
        System.out.println("Hash length " + hashLength(c.hashCode()));
        System.out.println("Str length " + stringLength(c));

        System.out.println(d.hashCode());
        System.out.println("Hash length " + hashLength(d.hashCode()));

        System.out.println("Str length " + stringLength(d));

        System.out.println(Arrays.toString(c.getBytes()));

        System.out.println(Integer.toHexString(c.hashCode()));

        int x = a.hashCode();

    }

    public static Integer noNegatives(Integer negativeInt) {
        if (negativeInt < 0) {
            negativeInt *= -1;
        }
        return negativeInt;

    }

    public static int stringLength(String password) {
        int pl = password.length();
        if (pl < 13) {
            System.out.println("Hash Length is 9");
            modHash(true);

        } else {
            System.out.println("Hash Length is 10");
            modHash(false);

        }
        return password.length();
    }

    public static int hashLength(int hashCode) {
        int length = noNegatives(String.valueOf(hashCode).length());
        if(length <9){
            
        }
        
        return length;
    }

    public static void modHash(Boolean mod) {
        if (mod) {
            
        }
        else{
            
        }
    }
}
