/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package murach.business;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; 

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product item; 

    private int quantity; 

    public int getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Product getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setItem(Product item) {
        this.item = item;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    

}