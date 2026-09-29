package murach.dibu;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import murach.business.Cart;
import murach.business.CartItem;
import murach.business.Customer;
import murach.business.Product;

public class CartDB {
    
    public static Cart getCart(Customer customer) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        try {
            Customer dbCustomer = em.find(Customer.class, customer.getCustomer_id());
            Cart cart = dbCustomer.getCart();
            
            if (cart == null) {
                EntityTransaction trans = em.getTransaction();
                trans.begin();
                cart = new Cart();
                cart.setCustomer(dbCustomer);
                em.persist(cart);
                trans.commit();
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

    // 4. Xóa hẳn một sản phẩm khỏi giỏ hàng
    public static void removeItem(int cartItemId) {
        EntityManager em = DBUtil.getEmFactory().createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            CartItem item = em.find(CartItem.class, cartItemId);
            if (item != null) {
                // ĐỒNG BỘ CACHE: Bốc khỏi danh sách trong RAM trước khi xóa dưới DB
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