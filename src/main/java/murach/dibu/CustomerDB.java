/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package murach.dibu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import murach.business.Customer;
public class CustomerDB {
    public static Customer getUserById(long customer_id){
        EntityManager em=DBUtil.getEmFactory().createEntityManager();
        try{
            Customer customer=em.find(Customer.class,customer_id);
            return customer;
        }finally{
            em.close();
        }
        
    }
    public static boolean insertCustomer(Customer customer) {
    EntityManager em = DBUtil.getEmFactory().createEntityManager();
    EntityTransaction trans = em.getTransaction();
    try {
        trans.begin();
        em.persist(customer);
        trans.commit();
        return true;
    } catch (Exception e) {
        System.out.println(e);
        if (trans.isActive()) trans.rollback();
        return false;
    } finally {
        em.close();
    }
    }
    public static Customer getCustomerByAccount(String account) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.account = :acc", Customer.class);
            query.setParameter("acc", account);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Không tìm thấy user
        } finally {
            em.close();
        }
    }
    public static Customer login(String account, String password) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.account = :acc AND c.password = :pass", Customer.class);
            query.setParameter("acc", account);
            query.setParameter("pass", password);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; 
        } finally {
            em.close();
        }
    }
    
}
