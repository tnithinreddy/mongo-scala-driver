/*
 * Copyright 2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.scala.gridfs

import java.nio.ByteBuffer

import org.bson.types.ObjectId
import com.mongodb.async.SingleResultCallback
import com.mongodb.async.client.gridfs.{ GridFSUploadStream => JGridFSUploadStream }

import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.internal.ObservableHelper.{ observeCompleted, observeInt }
import org.mongodb.scala.{ Completed, Observable }

/**
 * A GridFS OutputStream for uploading data into GridFS
 *
 * Provides the id for the file to be uploaded as well as the write methods of a AsyncOutputStream
 *
 * @since 1.2
 */
case class GridFSUploadStream(private val wrapped: JGridFSUploadStream) extends AsyncOutputStream {

  /**
   * Gets the ObjectId for the file to be uploaded
   *
   * @throws MongoGridFSException if the file id is not an ObjectId.
   *
   * @return the ObjectId for the file to be uploaded
   */
  lazy val objectId: ObjectId = wrapped.getObjectId

  /**
   * The BsonValue id for this file.
   *
   * @return the id for this file
   */
  lazy val id: BsonValue = wrapped.getId

  /**
   * Aborts the upload and deletes any data.
   *
   * @return an Observable identifying when the abort and cleanup has finished
   */
  def abort(): Observable[Completed] = observeCompleted(wrapped.abort(_: SingleResultCallback[Void]))

  /**
   * Writes a sequence of bytes from the given buffer into this stream.
   *
   * @param src the source buffer containing the data to be written.
   * @return a Observable returning a single element containing the number of bytes written.
   */
  override def write(src: ByteBuffer): Observable[Int] = observeInt(wrapped.write(src, _: SingleResultCallback[java.lang.Integer]))

  /**
   * Closes the output stream
   *
   * @return an Observable identifying when the AsyncOutptStream has been closed
   */
  override def close(): Observable[Completed] = observeCompleted(wrapped.close(_: SingleResultCallback[Void]))
}

