package com.example.warehouse.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "order_tracking")
class OrderTracking(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    var order: Order,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var currentStatus: OrderStatus = OrderStatus.CREATED,

    var createdAt: LocalDateTime? = null,
    var confirmedAt: LocalDateTime? = null,
    var pickingAt: LocalDateTime? = null,
    var packedAt: LocalDateTime? = null,
    var shippedAt: LocalDateTime? = null,
    var deliveredAt: LocalDateTime? = null,
    var cancelledAt: LocalDateTime? = null
)