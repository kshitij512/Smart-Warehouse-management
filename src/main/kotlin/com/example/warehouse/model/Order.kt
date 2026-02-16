package com.example.warehouse.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.time.LocalDateTime

@Entity
@Table(
    name = "orders",
    indexes = [Index(name = "idx_order_status", columnList = "status")]
)
data class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.CREATED,

    @Column(nullable = false)
    var totalAmount: Double,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    var warehouse: Warehouse,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    var assignedStaff: User? = null,

    @OneToMany(
        mappedBy = "order",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var orderItems: MutableList<OrderItem> = mutableListOf()
)
