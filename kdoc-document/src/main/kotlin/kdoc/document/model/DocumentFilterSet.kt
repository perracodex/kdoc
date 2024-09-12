/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.document.model

import kdoc.base.database.schema.document.types.DocumentType
import kdoc.base.persistence.serializers.SUuid
import kotlinx.serialization.Serializable

/**
 * A set of filters that can be applied to a document query.
 * All fields are optional, so that the filter can be used with any data combination.
 *
 * @property id The ID of the document.
 * @property ownerId The ID of the actor who owns the document.
 * @property groupId The group to which the document belongs, allowing documents to be associated.
 * @property name The name of the document.
 * @property type The [DocumentType] of the document.
 * @property description The description of the document.
 */
@Serializable
public data class DocumentFilterSet(
    val id: SUuid? = null,
    val ownerId: SUuid? = null,
    val groupId: SUuid? = null,
    val name: String? = null,
    val type: List<DocumentType>? = null,
    val description: String? = null
)
