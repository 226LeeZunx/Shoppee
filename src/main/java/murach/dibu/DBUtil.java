/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package murach.dibu;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DBUtil {
    private static final EntityManagerFactory emf=Persistence.createEntityManagerFactory("emailListPU");
    public static EntityManagerFactory getEmFactory(){
        return emf;
    }
    
}
