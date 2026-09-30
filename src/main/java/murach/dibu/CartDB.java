package murach.dibu;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import murach.business.Cart;
import murach.business.CartItem;
import murach.business.Customer;
import murach.business.Product;

public class CartDB {
    
    public static Cart getCart(Customer customer) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            TypedQuery<Cart> query = em.createQuery(
                "SELECT c FROM Cart c WHERE c.customer.customer_id = :customerId", Cart.class);
            query.setParameter("customerId", customer.getCustomer_id());
            List<Cart> carts = query.getResultList();
            
            Cart cart = null;
            if (carts != null && !carts.isEmpty()) {
                cart = carts.get(0);
            }
            
            if (cart == null) {
                Customer dbCustomer = em.find(Customer.class, customer.getCustomer_id());
                if (dbCustomer != null) {
                    EntityTransaction trans = em.getTransaction();
                    try {
                        trans.begin();
                        cart = new Cart();
                        cart.setCustomer(dbCustomer);
                        em.persist(cart);
                        trans.commit();
                    } catch (Exception e) {
                        if (trans.isActive()) trans.rollback();
                        e.printStackTrace();
                    }
                }
            }
            return cart;
        } finally {
            em.close();
        }
    }

    public static void addOrUpdateItem(Cart cart, int productId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            
            Cart dbCart = em.find(Cart.class, cart.getCart_id());
            Product product = em.find(Product.class, productId);
            
            if (dbCart == null || product == null) {
                trans.rollback();
                return;
            }

            boolean isExist = false;
            if (dbCart.getItems() != null) {
                for (CartItem item : dbCart.getItems()) {
                    if (item.getVariant().getProduct_id() == productId) {
                        item.setQuantity(item.getQuantity() + 1); 
                        isExist = true;
                        break;
                    }
                }
            }
            
            if (!isExist) {
                CartItem newItem = new CartItem();
                newItem.setCart(dbCart);
                newItem.setVariant(product);
                newItem.setQuantity(1);
                em.persist(newItem);
                
                if (dbCart.getItems() == null) {
                    dbCart.setItems(new java.util.ArrayList<>());
                }
                dbCart.getItems().add(newItem);
            }
            
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void updateQuantity(int cartItemId, int qty) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            CartItem item = em.find(CartItem.class, cartItemId);
            if (item != null) {
                if (qty > 0) {
                    item.setQuantity(qty);
                } else {
                    Cart parentCart = item.getCart();
                    if (parentCart != null && parentCart.getItems() != null) {
                        parentCart.getItems().remove(item);
                    }
                    em.remove(item); 
                }
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void removeItem(int cartItemId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            CartItem item = em.find(CartItem.class, cartItemId);
            if (item != null) {
                Cart parentCart = item.getCart();
                if (parentCart != null && parentCart.getItems() != null) {
                    parentCart.getItems().remove(item);
                }
                em.remove(item);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}