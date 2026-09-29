/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package murach.business;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int product_id;

    private String variant_name;
    private double price;

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getVariant_name() {
        return variant_name;
    }

   

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setVariant_name(String variant_name) {
        this.variant_name = variant_name;
    }

}