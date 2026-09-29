package murach.dibu;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;
import murach.business.*;

public class OrderDB {
    public static Order checkout(Customer sessionCustomer) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Customer customer = em.find(Customer.class, sessionCustomer.getCustomer_id());
            Cart cart = customer.getCart();

            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                trans.rollback();
                return null;
            }

            Order newOrder = new Order();
            newOrder.setCustomer(customer);
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem cItem : cart.getItems()) {
                OrderItem oItem = new OrderItem();
                oItem.setOrder(newOrder);
                oItem.setItem(cItem.getVariant());
                oItem.setQuantity(cItem.getQuantity());
                orderItems.add(oItem);
                
                em.remove(cItem); 
            }
            
            newOrder.setItems(orderItems);
            em.persist(newOrder);
            
            cart.getItems().clear();

            trans.commit();
            return newOrder;
        } catch (Exception e) {
            e.printStackTrace();
            if (trans.isActive()) trans.rollback();
            return null;
        } finally {
            em.close();
        }
    }
    public static java.util.List<Order> getOrdersByCustomer(int customerId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            jakarta.persistence.TypedQuery<Order> query = em.createQuery(
                "SELECT o FROM Order o WHERE o.customer.customer_id = :cid ORDER BY o.order_id DESC", Order.class);
            query.setParameter("cid", customerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}