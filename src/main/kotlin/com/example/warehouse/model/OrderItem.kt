package com.example.warehouse.model


import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
@Table(name = "order_items")
data class OrderItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(nullable = false)
    var quantity: Int,

    @Column(nullable = false)
    var price: Double
)
