using System.ComponentModel.DataAnnotations;


namespace WebMVCHttpPostFile.Models
{
    public class FileUploadModel
    {
        // begin
        [DataType(DataType.Upload)]
        [Display(Name = "Upload File")]
        [Required(ErrorMessage = "Please choose file to upload.")]
        public string file { get; set; }
        // end
    }
}