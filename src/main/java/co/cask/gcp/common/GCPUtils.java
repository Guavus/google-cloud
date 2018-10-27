/*
 * Copyright © 2018 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.gcp.common;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigquery.BigQueryOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.Nullable;

/**
 * GCP utility class to get service account credentials
 */
public class GCPUtils {

  public static ServiceAccountCredentials loadCredentials(String path) throws IOException {
    File credentialsPath = new File(path);
    try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
      return ServiceAccountCredentials.fromStream(serviceAccountStream);
    }
  }

  /**
   * If project id is not provided and cannot be detected from environment, throw an exception indicating that else
   * return the project id.
   */
  public static String getProjectId(@Nullable String project) {
    String projectId = project == null || project.isEmpty() ? ServiceOptions.getDefaultProjectId() : project;
    if (projectId == null) {
      throw new IllegalArgumentException(
        "Could not detect Google Cloud project id from the environment. Please specify a project id.");
    }
    return projectId;
  }

  /**
   *
   * @param projectId
   * @param serviceFilePath
   * @return
   * @throws Exception
   */
  public static BigQueryOptions.Builder getBigQuery(String projectId, String serviceFilePath) throws Exception {
    BigQueryOptions.Builder builder = BigQueryOptions.newBuilder();
    if (serviceFilePath != null) {
      builder.setCredentials(loadCredentials(serviceFilePath));
    }
    String project = projectId == null ? ServiceOptions.getDefaultProjectId() : projectId;
    if (project == null) {
      throw new Exception("Could not detect Google Cloud project id from the environment. " +
                            "Please specify a project id.");
    }
    builder.setProjectId(project);
    return builder;
  }
}
