package murach.business;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customer_id;

    @Column(unique = true, nullable = false)
    private String account; 

    private String first_name; 
    private String last_name; 
    private String password; 
    private String email;
    private String gender; 
    

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;
    
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private Cart cart;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    

}