/**
  * Copyright 2020 Marco Vermeulen
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package steps

import cucumber.api.scala.{EN, ScalaDsl}
import io.sdkman.model.{Candidate, Version}
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import support.Mongo

class PersistenceSteps extends ScalaDsl with EN with Matchers with OptionValues {

  Then("""^(.*) Version (.*) with URL (.*) was published as (.*)$""") {
    (candidate: String, version: String, url: String, platform: String) =>
      withClue("Version was not published") {
        Mongo.versionPublished(candidate, version, url, platform) shouldBe true
      }
  }

  Then("""^the (.*) (.*) Version (.*) has a vendor of '(.*)'$""") {
    (platform: String, candidate: String, version: String, vendor: String) =>
      withClue(s"Vendor does not match $vendor") {
        Mongo.versionVendor(candidate, version, platform).value shouldBe vendor
      }
  }

  Then("""^the (.*) (.*) Version (.*) has no vendor$""") {
    (platform: String, candidate: String, version: String) =>
      withClue(s"Should not have a vendor") {
        Mongo.versionVendor(candidate, version, platform) shouldBe None
      }
  }

  Then("""^(.*) Version (.*) is hidden""") { (candidate: String, version: String) =>
    withClue("Version is not hidden") {
      Mongo.versionVisible(candidate, version) shouldBe false
    }
  }

  Then("""^(.*) Version (.*) is visible$""") { (candidate: String, version: String) =>
    withClue("Version is not hidden") {
      Mongo.versionVisible(candidate, version) shouldBe true
    }
  }

  Given("""^a (.*) (.*) Version (.*) with URL (.*) already exists$""") {
    (platform: String, candidate: String, version: String, url: String) =>
      Mongo.insertVersion(
        Version(candidate, version, platform, s"http://somecandidate.org/$candidate/$version")
      )
  }

  Given("""^an existing (.*) (.*) Version (.*) exists""") {
    (platform: String, candidate: String, version: String) =>
      Mongo.insertVersion(
        Version(
          candidate = candidate,
          version = version,
          platform = platform,
          url = s"http://somecandidate.org/$candidate/$version",
          visible = Some(true)
        )
      )
  }

  Given("""^the (.*) candidate (.*) with default version (.*) already exists$""") {
    (platform: String, candidate: String, version: String) =>
      Mongo.insertCandidate(
        Candidate(
          candidate = candidate,
          name = candidate.capitalize,
          description = s"$candidate description",
          default = Some(version),
          websiteUrl = s"http://somecandidate.org/$candidate",
          distribution = platform
        )
      )
  }

  Given("""^the existing Default (.*) (.*) Version is (.*)$""") {
    (platform: String, candidate: String, version: String) =>
      Mongo.insertCandidate(
        Candidate(
          candidate = candidate,
          name = candidate.capitalize,
          description = s"$candidate description",
          default = Some(version),
          websiteUrl = s"http://somecandidate.org/$candidate",
          distribution = platform
        )
      )
  }

  Given("""^the existing (.*) (.*) Version has no Default$""") {
    (platform: String, candidate: String) =>
      Mongo.insertCandidate(
        Candidate(
          candidate = candidate,
          name = candidate.capitalize,
          description = s"$candidate description",
          default = None,
          websiteUrl = s"http://somecandidate.org/$candidate",
          distribution = platform
        )
      )
  }

  Then("""^the Default (.*) Version has changed to (.*)$""") {
    (candidate: String, version: String) =>
      withClue(s"The default $candidate version was not changed to $version") {
        Mongo.isDefault(candidate, version) shouldBe true
      }
  }

  Given("""^the (.*) version (.*) (.*) does not exist$""") {
    (candidate: String, version: String, platform: String) =>
      withClue(s"$candidate $version does not exist") {
        Mongo.versionExists(candidate, version, platform) shouldBe false
      }
  }

  Given("""^the (.*) version (.*) (.*) still exists$""") {
    (candidate: String, version: String, platform: String) =>
      withClue(s"$candidate $version does not exist") {
        Mongo.versionExists(candidate, version, platform) shouldBe true
      }
  }

  Given("""^Candidate (.*) does not exist$""") { candidate: String =>
    withClue(s"The exists: $candidate") {
      Mongo.candidateExists(candidate) shouldBe false
    }
  }

  Given("""^an alive OK entry in the application collection$""") { () =>
    Mongo.insertAliveOk()
  }

  Then("""^(.*) Version (.*) on platform (.*) has a checksum \"(.*)\" using algorithm (.*)$""") {
    (candidate: String, version: String, platform: String, checksum: String, algorithm: String) =>
      withClue("Checksum not found") {
        Mongo.checksumExists(candidate, version, platform, algorithm, checksum) shouldBe true
      }
  }
}
