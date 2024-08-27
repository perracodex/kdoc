/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdoc.base.database.schema.document

import kdoc.base.persistence.utils.autoGenerate
import kdoc.base.persistence.utils.kotlinUuid
import kdoc.base.utils.KLocalDateTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import kotlin.uuid.Uuid

/**
 * Database table definition to track document audit logs.
 */
public object DocumentAuditTable : Table(name = "document_audit") {
    /** The record unique identifier. */
    public val id: Column<Uuid> = kotlinUuid(
        name = "audit_id"
    ).autoGenerate()

    /** The operation that was performed. */
    public val operation: Column<String> = varchar(
        name = "operation",
        length = 512
    )

    /** The unique identifier of the actor that performed the operation. */
    public val actorId: Column<Uuid?> = kotlinUuid(
        name = "actor_id"
    ).nullable()

    /** The unique identifier of the document. */
    public val documentId: Column<Uuid?> = kotlinUuid(
        name = "document_id"
    ).nullable()

    /** The unique identifier of the group that the document belongs to. */
    public val groupId: Column<Uuid?> = kotlinUuid(
        name = "group_id"
    ).nullable()

    /** The unique identifier of the owner of the document. */
    public val ownerId: Column<Uuid?> = kotlinUuid(
        name = "owner_id"
    ).nullable()

    /** Additional log information. */
    public val log: Column<String?> = text(
        name = "log"
    ).nullable()

    /** The date and time the audit log entry was created. */
    public val createdAt: Column<KLocalDateTime> = datetime(
        name = "created_at"
    ).defaultExpression(defaultValue = CurrentDateTime)

    override val primaryKey: Table.PrimaryKey = PrimaryKey(
        firstColumn = id,
        name = "pk_audit_id"
    )
}
