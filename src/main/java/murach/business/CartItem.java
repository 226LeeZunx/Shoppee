/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package murach.business;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; 

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product variant; 

    private int quantity;

    public int getId() {
        return id;
    }

    public Cart getCart() {
        return cart;
    }

    public Product getVariant() {
        return variant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void setVariant(Product variant) {
        this.variant = variant;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}