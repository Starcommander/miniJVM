package test;

import java.io.IOException;
//import java.util.Scanner;

class HelloConsole {

    public static void main(String[] args) {
        byte[] buffer = new byte[128];
        System.out.println("Hello console");
        System.out.println("Enter a name:");
        try
        {
          int count = System.in.read(buffer);
          if (count > 0)
          {
            String name = new String(buffer, 0, count);
            System.out.println("Your name: '" + name + "' has " + name.length() + " digits.");
            askAge();
          }
          else
          {
            System.out.println("You have no name.");
          }
        }
        catch (IOException e)
        {
          System.err.println("Error reading input.");
        }
    }

    private static void askAge() throws IOException
    {
      byte[] buffer = new byte[32];
      System.out.println("Enter your age:");
      int count = System.in.read(buffer);
      if (count > 0)
      {
        String ageS = new String(buffer, 0, count);
        try
        {
          int age = Integer.parseInt(ageS);
          if (age >= 50) { System.out.println("You are an old man/woman. (>=50)."); }
          else { System.out.println("You are a young guy/girl. (<50)."); }
        } catch (Exception e) { System.out.println("You dont have a valid age."); }
      }
      else
      {
        System.out.println("You dont have any age.");
      }
    }

}
