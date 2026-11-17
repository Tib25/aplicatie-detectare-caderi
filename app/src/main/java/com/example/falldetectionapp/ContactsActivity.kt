package com.example.falldetectionapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

//definim clasa ce controleaza ecranul pentru lista cu persoanele de contact
class ContactsActivity : AppCompatActivity() {

    private lateinit var llContacts: LinearLayout //containerul in care afisam contactele
    private lateinit var etSearchContact: EditText //campul in care utilizatorul cauta persoane

    private val contacts = mutableListOf<Contact>() //cream o lista in care pastram toate persoanele de contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts) //incarca interfata definita in activity_contacts

        llContacts = findViewById(R.id.llContacts)
        etSearchContact = findViewById(R.id.etSearchContact)

        loadContacts() //incarca persoanele salvate anterior
        displayContacts() //afiseaza persoanele in lista

        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            showContactDialog()
        } //identifica butonul pt adaugare de contact si deschide chenarul pentru a introduce detaliile lui

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        } //daca apasam butonul inapoi inchide ecranul de contacte si revine la pagina principala

        etSearchContact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {} //adaugam un mecanism care detecteaza modificarile efectuate in bara de cautare

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                displayContacts(s.toString())
            } //apelata atunci cand utilizatorul modifica textul si actualizeaza lista pentru a afisa doar contactele care corespund cautarii

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    //functie ce se ocupa de afisarea si filtrarea persoanelor de contact
    private fun displayContacts(search: String = "") {
        llContacts.removeAllViews() //sterge elementele afisate anterior pentru a putea construi din nou lista actualizata

        val filteredContacts = contacts.filter {
            it.name.contains(search, ignoreCase = true)
        } //filtreaza lista si păstreaza doar persoanele ale caror nume contin textul cautat

        if (filteredContacts.isEmpty()) { //daca lista e goala afisam un mesaj
            val emptyText = TextView(this).apply {
                text = if (search.isEmpty()) {
                    "Nu ai încă persoane de contact."
                } else {
                    "Nu am găsit niciun contact."
                }
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(16, 32, 16, 32)
            }
            llContacts.addView(emptyText) //adauga mesajul in interfata
        }

        filteredContacts.forEach { contact -> //parcurge fiecare contact gasit in urma filtrarii
            val contactButton = Button(this).apply {
                //creeaza un buton pentru fiecare persoana in care afiseaza numele si numarul de telefon
                text = "${contact.name}\n${contact.phone}"
                textSize = 16f
                gravity = Gravity.CENTER_VERTICAL
                isAllCaps = false

                setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#DCEAF5")
                    )
                )

                setTextColor(
                    android.graphics.Color.parseColor("#183B5B")
                )

                setOnClickListener {
                    showContactDetails(contact)
                } //la apasarea unui contact deschide fereastra cu detaliile persoanei respective
            }

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ) //stabileste dimensiunile butonului
            params.bottomMargin = 8

            llContacts.addView(contactButton, params) //adauga butonul contactului in lista vizibila
        }
    }

    //functie ce gestioneaza adaugarea si editarea persoanelor de contact
    private fun showContactDialog(contact: Contact? = null) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 16, 40, 0)
        } //creeaza un container vertical in care vor fi introduse câmpurile formularului

        val nameInput = EditText(this).apply {
            hint = "Nume și prenume"
            setSingleLine(true)
            setText(contact?.name ?: "")
        } //Creează câmpul pentru numele persoanei

        val phoneInput = EditText(this).apply {
            hint = "Număr de telefon (10 cifre)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(
                android.text.InputFilter.LengthFilter(10)
            )
            setSingleLine(true)
            setText(contact?.phone ?: "")
        } //Creează câmpul pentru numărul de telefon și afișează tastatura numerică

        layout.addView(nameInput) //adaugam campul pentru nume in formular
        layout.addView(phoneInput) //adaugam campul pentru nr de tel in formular

        val errorMessage = TextView(this).apply {
            textSize = 12f
            setTextColor(android.graphics.Color.parseColor("#D94343"))
            visibility = android.view.View.GONE
            setPadding(4, 4, 4, 8)
        } //Creează un câmp pentru afișarea mesajelor de eroare, cu text roșu ce initial e ascuns

        layout.addView(errorMessage) //Adaugă mesajul de eroare sub câmpurile formularului

        val dialog = AlertDialog.Builder(this) //Construiește fereastra de dialog
            .setTitle(if (contact == null) "Adaugă persoană" else "Editează persoana")
            .setView(layout)
            .setPositiveButton("Salvează", null)
            .setNegativeButton("Anulează", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { //Setează acțiunea butonului Salvează atunci când este apăsat
                val name = nameInput.text.toString().trim() //preia textul de la nume si elimina spatiile de la inceput si final
                val phone = phoneInput.text.toString().trim() //la fel ca la nume


                if (name.isEmpty() && phone.isEmpty()) {
                    errorMessage.text = "Completați câmpurile obligatorii."
                    errorMessage.visibility = android.view.View.VISIBLE

                    android.os.Handler(mainLooper).postDelayed({
                        errorMessage.visibility = android.view.View.GONE
                    }, 3000)

                    return@setOnClickListener //Oprește executarea acțiunii de salvare, deoarece datele nu sunt valide
                }

                if (name.isEmpty()) {

                    nameInput.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#D94343")
                        )
                    errorMessage.text = "Introduceți numele persoanei."

                    errorMessage.visibility = android.view.View.VISIBLE

                    android.os.Handler(mainLooper).postDelayed({
                        errorMessage.visibility = android.view.View.GONE
                    }, 3000)

                    return@setOnClickListener
                }

                if (!phone.matches(Regex("^0[0-9]{9}$"))) {

                    phoneInput.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#D94343")
                        )
                    errorMessage.text =
                        "Numărul trebuie să conțină 10 cifre și să înceapă cu 0."

                    errorMessage.visibility = android.view.View.VISIBLE

                    android.os.Handler(mainLooper).postDelayed({
                        errorMessage.visibility = android.view.View.GONE
                    }, 3000)

                    return@setOnClickListener
                }

                if (contact == null) { //Creează un contact nou cu ID unic, nume și număr de telefon și îl adaugă în listă
                    contacts.add(
                        Contact(UUID.randomUUID().toString(), name, phone)
                    )
                } else { //Dacă este vorba despre editare, caută poziția contactului existent în listă, folosind ID-ul său
                    val index = contacts.indexOfFirst { it.id == contact.id }

                    if (index != -1) {
                        contacts[index] = contact.copy(
                            name = name,
                            phone = phone
                        )
                    }
                }

                saveContacts()
                displayContacts(etSearchContact.text.toString())

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    //functie ce afișează informațiile unei persoane atunci când utilizatorul apasă pe contactul respectiv
    private fun showContactDetails(contact: Contact) {
        AlertDialog.Builder(this) //Creează o fereastră care afișează numele și numărul de telefon al persoanei
            .setTitle(contact.name)
            .setMessage("Număr de telefon: ${contact.phone}")
            .setPositiveButton("Editează") { _, _ ->
                showContactDialog(contact)
            }
            .setNegativeButton("Închide", null)
            .setNeutralButton("Șterge", null)
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_NEUTRAL).apply {

                        // Culoarea roșie a butonului Șterge
                        setTextColor(
                            android.graphics.Color.parseColor("#D94343")
                        )

                        setOnClickListener {
                            AlertDialog.Builder(this@ContactsActivity)//Creează o nouă fereastră pentru confirmarea ștergerii.
                                .setTitle("Ștergere contact")
                                .setMessage("Sigur dorești să ștergi?")
                                .setNegativeButton("Anulează", null)
                                .setPositiveButton("Șterge") { _, _ ->
                                    contacts.removeAll { it.id == contact.id }
                                    saveContacts()
                                    displayContacts(etSearchContact.text.toString())
                                }
                                .show()

                            dismiss()
                        }
                    }
                }
                show()
            }
    }

    //functie ce salvează contactele pentru ca acestea să nu dispară după închiderea aplicației
    private fun saveContacts() {
        val array = JSONArray() //Creează un array JSON în care vom introduce toate persoanele de contact

        contacts.forEach { contact ->
            val obj = JSONObject() //Creează un obiect JSON pentru contactul curent
            obj.put("id", contact.id)
            obj.put("name", contact.name)
            obj.put("phone", contact.phone)
            array.put(obj)
        }

        getSharedPreferences("SafeStepContacts", MODE_PRIVATE) //accesează memoria locală a aplicației folosind spațiul de stocare
            .edit()
            .putString("contacts", array.toString())
            .apply()
    }

    //funcție ce încarcă persoanele salvate anterior
    private fun loadContacts() {
        val savedData = getSharedPreferences(
            "SafeStepContacts",
            MODE_PRIVATE
        ).getString("contacts", null) ?: return

        try {
            val array = JSONArray(savedData)

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)

                contacts.add(
                    Contact(
                        obj.getString("id"),
                        obj.getString("name"),
                        obj.getString("phone")
                    )
                )
            }
        } catch (e: Exception) {
            contacts.clear()
        }
    }
}
