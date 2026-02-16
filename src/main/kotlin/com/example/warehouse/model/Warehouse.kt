package com.example.warehouse.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(
    name = "warehouses",
    indexes = [Index(name = "idx_warehouse_location", columnList = "location")]
)
data class Warehouse(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var location: String,

    @Column(nullable = false)
    var capacity: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    var manager: User,

//    @OneToMany(fetch = FetchType.LAZY)
//    val staff: List<Staff>

)
