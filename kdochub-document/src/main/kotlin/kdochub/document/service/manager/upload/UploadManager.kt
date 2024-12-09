/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package kdochub.document.service.manager.upload

import io.ktor.http.content.*
import kdochub.core.context.SessionContext
import kdochub.core.env.Tracer
import kdochub.database.schema.document.type.DocumentType
import kdochub.document.error.DocumentError
import kdochub.document.model.Document
import kdochub.document.model.DocumentRequest
import kdochub.document.repository.IDocumentRepository
import kdochub.document.service.manager.upload.annotation.UploadApi
import kotlin.uuid.Uuid

/**
 * Handles the uploading and processing of document files into the storage.
 *
 * @see [MultipartFileHandler]
 */
internal class UploadManager(
    @Suppress("unused") private val sessionContext: SessionContext,
    private val documentRepository: IDocumentRepository
) {
    private val tracer: Tracer = Tracer<UploadManager>()

    /**
     * Handles the creation of documents from multipart data.
     * If a group ID is provided, the uploaded files are associated with that group,
     * otherwise a new group ID is generated and associated with the files.
     *
     * If any file persistence fails, all saved in this operation are deleted.
     *
     * @param ownerId The ID of the owner of the document.
     * @param groupId Optional group ID to associate with the uploaded files.
     * @param type The [DocumentType] being uploaded.
     * @param uploadRoot The root path where uploaded files are stored.
     * @param cipher Whether the document should be ciphered.
     * @param multipart The multipart data containing the files and request.
     * @return A list of created [Document] entries or null if the request is invalid.
     */
    @OptIn(UploadApi::class)
    suspend fun upload(
        ownerId: Uuid,
        groupId: Uuid? = null,
        type: DocumentType,
        uploadRoot: String,
        cipher: Boolean,
        multipart: MultiPartData
    ): List<Document> {

        // Receive the uploaded files.

        val persistedFiles: List<MultipartFileHandler.Response> = MultipartFileHandler(
            uploadsRoot = uploadRoot,
            cipher = cipher
        ).receive(ownerId = ownerId, groupId = groupId, type = type, multipart = multipart)

        if (persistedFiles.isEmpty()) {
            tracer.error("No files provided for upload.")
            throw DocumentError.NoDocumentProvided(ownerId = ownerId)
        }

        // Create the document references in the database.

        try {
            val output: MutableList<Document> = mutableListOf()
            val targetGroupId: Uuid = groupId ?: Uuid.random()

            persistedFiles.forEach { fileEntry ->
                val documentRequest = DocumentRequest(
                    ownerId = ownerId,
                    groupId = targetGroupId,
                    type = type,
                    description = fileEntry.description,
                    originalName = fileEntry.originalFilename,
                    storageName = fileEntry.storageFilename,
                    location = fileEntry.location,
                    isCiphered = fileEntry.isCiphered,
                    size = fileEntry.size
                )

                val newDocument: Document = documentRepository.create(request = documentRequest)
                output.add(newDocument)
            }

            return output
        } catch (e: Exception) {
            tracer.error("Error uploading document: $e")
            // If any file persistence fails, delete all saved files.
            persistedFiles.forEach { it.delete() }
            throw DocumentError.FailedToPersistUpload(ownerId = ownerId, cause = e)
        }
    }
}
