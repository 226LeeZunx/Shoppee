package murach.dibu;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import murach.business.Product;

public class ProductDB {
    
    public static List<Product> getAllProducts() {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p", Product.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}