using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WebMVCHttpPostFile.Models
{
    public class ModelFactoryTestImage
    {
        public static TestImage CreateTestImage(string id, string fileName)
        {
            TestImage myTestImage = new TestImage();
            myTestImage.HiveId = id;
            myTestImage.ImageName = fileName;

            return myTestImage;
        }

        public static readonly TestImage Default = new TestImage()
        {
            HiveId = "Default", 
            ImageName = "default.jpg"
        };
    }
}