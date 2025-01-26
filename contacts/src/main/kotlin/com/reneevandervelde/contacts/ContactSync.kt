package com.reneevandervelde.contacts

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.provider.ContactsContract
import com.reneevandervelde.contacts.settings.NotionSettings
import com.reneevandervelde.notion.*
import com.reneevandervelde.notion.database.DatabaseQuery
import com.reneevandervelde.notion.page.PageFilter
import com.reneevandervelde.notion.page.TextFilter
import com.reneevandervelde.notion.page.ValueFilter
import kotlinx.coroutines.flow.*
import regolith.data.settings.SettingsAccess
import regolith.data.settings.observeSetting
import regolith.processes.daemon.Daemon
import java.lang.StringBuilder

private const val ACCOUNT_NAME = "Notion Contacts"
private const val ACCOUNT_TYPE = "com.reneevandervelde.contacts"
private const val MIME_CONTACT_ID = "vnd.android.cursor.item/vnd.com.reneevandervelde.contacts.id"

class ContactSync(
    private val contentResolver: ContentResolver,
    notion: NotionApi,
    settingsAccess: SettingsAccess,
): Daemon {
    private val apiKeySetting = settingsAccess.observeSetting(NotionSettings.apiKey)
        .filterNotNull()
    private val databaseIdSetting = settingsAccess.observeSetting(NotionSettings.databaseId)
        .filterNotNull()
    private val contacts = combine(
        apiKeySetting,
        databaseIdSetting,
    ) { apiKey, databaseId ->
        notion.queryDatabaseForAll(
            token = apiKey,
            database = databaseId,
            query = DatabaseQuery(
                filter = PageFilter.And(
                    PageFilter.Or(
                        PageFilter.PhoneNumber(
                            property = ContactPage.Properties.Phone,
                            filter = ValueFilter.IsNotEmpty,
                        ),
                        PageFilter.Email(
                            property = ContactPage.Properties.Email,
                            filter = ValueFilter.IsNotEmpty,
                        ),
                        PageFilter.Email(
                            property = ContactPage.Properties.WorkEmail,
                            filter = ValueFilter.IsNotEmpty,
                        ),
                        PageFilter.Text(
                            property = ContactPage.Properties.Address,
                            filter = TextFilter.IsNotEmpty,
                        ),
                    ),
                    PageFilter.Text(
                        property = ContactPage.Properties.Name,
                        filter = TextFilter.IsNotEmpty,
                    )
                )
            ),
        )
    }.map {
        it.map {
            ContactPage(it)
        }
    }

    override suspend fun startDaemon(): Nothing
    {
        contacts.collectLatest {
            it.forEach { contact ->
                addOrUpdateContact(contact)
            }
        }
        throw IllegalStateException()
    }

    private fun addOrUpdateContact(
        contact: ContactPage,
    ) {
        println("Updating contact ${contact.name}")
        val existingContactId = getContactId(contact.id)


        contentResolver.setItemValue(
            contactId = existingContactId,
            mimeType = MIME_CONTACT_ID,
            key = ContactsContract.Data.DATA1,
            newValue = contact.id,
        )
        contentResolver.setItemValue(
            contactId = existingContactId,
            mimeType = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
            key = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
            newValue = contact.name,
        )
        contentResolver.setItemValue(
            contactId = existingContactId,
            mimeType = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            key = ContactsContract.CommonDataKinds.Phone.NUMBER,
            newValue = contact.phone,
            qualifiers = mapOf(
                ContactsContract.CommonDataKinds.Phone.TYPE to ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE.toString()
            )
        )
        contentResolver.setItemValue(
            contactId = existingContactId,
            mimeType = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
            key = ContactsContract.CommonDataKinds.Email.ADDRESS,
            newValue = contact.email,
            qualifiers = mapOf(
                ContactsContract.CommonDataKinds.Email.TYPE to ContactsContract.CommonDataKinds.Email.TYPE_HOME.toString()
            )
        )
        contentResolver.setItemValue(
            contactId = existingContactId,
            mimeType = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
            key = ContactsContract.CommonDataKinds.Email.ADDRESS,
            newValue = contact.workEmail,
            qualifiers = mapOf(
                ContactsContract.CommonDataKinds.Email.TYPE to ContactsContract.CommonDataKinds.Email.TYPE_WORK.toString()
            )
        )
        contentResolver.setItemValue(
            contactId = existingContactId,
            mimeType = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
            key = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
            newValue = contact.address,
            qualifiers = mapOf(
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE to ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME.toString()
            )
        )
        contentResolver.setItemValue(
            contactId = existingContactId,
            mimeType = ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE,
            key = ContactsContract.CommonDataKinds.Website.URL,
            newValue = contact.notionUrl,
            qualifiers = mapOf(
                ContactsContract.CommonDataKinds.Website.TYPE to ContactsContract.CommonDataKinds.Website.TYPE_PROFILE.toString()
            )
        )
        contentResolver.setItemValue(
            contactId = existingContactId,
            mimeType = ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
            key = ContactsContract.CommonDataKinds.Event.START_DATE,
            newValue = contact.birthDate,
            qualifiers = mapOf(
                ContactsContract.CommonDataKinds.Event.TYPE to ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString()
            )
        )
    }

    private fun ContentResolver.setItemValue(
        contactId: Long,
        mimeType: String,
        key: String,
        newValue: String?,
        qualifiers: Map<String, String> = emptyMap()
    ) {
        val qualifierKeys = qualifiers.keys
        val query = StringBuilder("${ContactsContract.Data.RAW_CONTACT_ID} = ?")
            .apply {
                append(" AND ${ContactsContract.Data.MIMETYPE} = ?")
                qualifierKeys.forEach {
                    append(" AND $it = ?")
                }
            }
            .toString()
        val args = arrayOf(
            contactId.toString(),
            mimeType,
            *qualifierKeys.map { qualifiers[it]!! }.toTypedArray(),
        )
        val existingResults = query(
            ContactsContract.Data.CONTENT_URI,
            null,
            query,
            args,
            null
        )
        val contentValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
            put(ContactsContract.Data.MIMETYPE, mimeType)
            put(key, newValue)
            qualifiers.forEach { (k, v) ->
                put(k, v)
            }
        }


        existingResults?.use { cursor ->
            if (cursor.moveToFirst()) {
                val currentValue = cursor.getString(cursor.getColumnIndexOrThrow(key))

                if (newValue.isNullOrBlank()) {
                    println("Deleting $mimeType")
                    delete(
                        ContactsContract.Data.CONTENT_URI,
                        query,
                        args
                    )
                    return
                }
                if (currentValue != newValue) {
                    println("Updating $mimeType value $newValue")
                    val updates = update(
                        ContactsContract.Data.CONTENT_URI,
                        contentValues,
                        query,
                        args
                    )
                    if (updates == 0) {
                        println("No value updated. Inserting $mimeType value $newValue")
                        insert(
                            ContactsContract.Data.CONTENT_URI,
                            contentValues
                        )
                    }
                } else {
                    println("Value $mimeType up-to-date")
                }
            } else {
                if (newValue.isNullOrBlank()) {
                    println("$mimeType not found. No action needed.")
                } else {
                    println("Inserting $mimeType value $newValue")
                    insert(
                        ContactsContract.Data.CONTENT_URI,
                        contentValues
                    )
                }
            }
            Unit
        }
    }

    private fun getContactId(pageId: String): Long {
        val projection = arrayOf(ContactsContract.Data.RAW_CONTACT_ID)
        val selection = "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.Data.DATA1} = ?"
        val selectionArgs = arrayOf(MIME_CONTACT_ID, pageId)

        contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Data.RAW_CONTACT_ID))
            }
        }

        val rawContactValues = ContentValues().apply {
            put(ContactsContract.RawContacts.ACCOUNT_NAME, ACCOUNT_NAME)
            put(ContactsContract.RawContacts.ACCOUNT_TYPE, ACCOUNT_TYPE)
        }
        val rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, rawContactValues)
        return ContentUris.parseId(rawContactUri!!)
    }
}
