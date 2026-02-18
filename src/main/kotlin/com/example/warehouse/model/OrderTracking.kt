package com.example.warehouse.model

import jakarta.persistence.Entity
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
data class OrderTracking(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    var createdAt: LocalDateTime? = null,
    var confirmedAt: LocalDateTime? = null,
    var pickingAt: LocalDateTime? = null,
    var packedAt: LocalDateTime? = null,
    var shippedAt: LocalDateTime? = null,
    var deliveredAt: LocalDateTime? = null,
    var cancelledAt: LocalDateTime? = null
)
