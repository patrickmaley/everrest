/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.everrest.spring;

import static org.junit.Assert.assertEquals;

import org.everrest.core.impl.provider.IOHelper;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class SpringResourceTest extends BaseTest
{
   @Path("a")
   public static class Resource
   {
      @GET
      public void m(Message m)
      {
         assertEquals(mesageBody, m.getMessage());
      }
   }

   public static class Message
   {
      private String message;

      public Message(String message)
      {
         this.message = message;
      }

      public String getMessage()
      {
         return message;
      }
   }

   @Provider
   public static class MessageProvider implements MessageBodyReader<Message>, MessageBodyWriter<Message>
   {

      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return Message.class == type;
      }

      public Message readFrom(Class<Message> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
         WebApplicationException
      {
         return new Message(IOHelper.readString(entityStream, mediaType != null ? mediaType.getParameters().get(
            "charset") : null));
      }

      public long getSize(Message t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return -1;
      }

      public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return Message.class.isAssignableFrom(type);
      }

      public void writeTo(Message t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
         WebApplicationException
      {
         IOHelper.writeString(t.getMessage(), entityStream, mediaType.getParameters().get("charset"));
      }

   }

   private static final String mesageBody = "TEST SPRING BEAN";

   @Test
   public void test() throws Exception
   {
      assertEquals(204, launcher.service("GET", "/a", "", null, mesageBody.getBytes(), null).getStatus());
   }

}