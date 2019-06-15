package pl.wojciechkabat.hotchilli.services

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils.asMap
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pl.wojciechkabat.hotchilli.CloudinaryConstants.*
import pl.wojciechkabat.hotchilli.dtos.PictureDto
import pl.wojciechkabat.hotchilli.entities.Picture
import pl.wojciechkabat.hotchilli.entities.User
import pl.wojciechkabat.hotchilli.exceptions.NoPictureWithGivenIdException
import pl.wojciechkabat.hotchilli.repositories.PictureRepository
import kotlin.collections.HashMap

@Service
class PictureServiceImpl(
        private val pictureRepository: PictureRepository
) : PictureService {
    private var cloudinary: Cloudinary = Cloudinary(
            asMap(
                    "cloud_name", CLOUDINARY_CLOUD_NAME,
                    "api_key", CLOUDINARY_API_KEY,
                    "api_secret", CLOUDINARY_API_SECRET)
    )
    private val LOG = LoggerFactory.getLogger(PictureService::class.java)

    override fun savePicture(pictureDto: PictureDto, user: User): Picture {
        return pictureRepository.save(Picture(null, pictureDto.externalIdentifier, pictureDto.url, user))
    }

    override fun deleteById(pictureId: Long) {
        val picture = pictureRepository.findById(pictureId).orElseThrow(({ NoPictureWithGivenIdException() }))
        if (picture.externalIdentifier != null) {
            deleteFromRemoteServerByExternalId(picture.externalIdentifier)
        }
        pictureRepository.deleteById(pictureId)
    }

    private fun deleteFromRemoteServerByExternalId(externalPictureId: String) {
        LOG.info("Cloudinary - Attempting to delete picture with id: $externalPictureId")
        try {
            cloudinary.api().deleteResources(listOf(externalPictureId), HashMap<String, String>())
        } catch (e: Exception) {
            LOG.error("Cloudinary error - Could not delete image with id: $externalPictureId")
        }
    }
}