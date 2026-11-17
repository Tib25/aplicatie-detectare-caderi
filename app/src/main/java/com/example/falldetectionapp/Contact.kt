package com.example.falldetectionapp

//definim modelul unei persoane de contact
data class Contact(
    val id: String, //folosim pentru editare sau stergere
    val name: String, //retine numele persoanei
    val phone: String //retine numarul de telefon
)