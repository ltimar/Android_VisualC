using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using WebMVCHttpPostFile.Models;

namespace WebMVCHttpPostFile.Controllers
{
    public class FileUploadController : Controller
    {
        // GET: FileUpload
        private Apiary_LoriEntities _monitorDBContex;
      
        private const string LOCAL_PATH = "/Monitor";

        public FileUploadController()
        {
            _monitorDBContex = new Apiary_LoriEntities();
        }

        public ActionResult Index()
        {
            return View();
           // return null;
        }
      
        [System.Web.Mvc.HttpGet]
        public ActionResult LoadImage(string id)
        {
            // seek an item from database, based on id and GET it (sent it) to the LoadPage
            if (id != null)
            {
                ViewBag.localPath = LOCAL_PATH+"/"+id;
                TestImage crtHiveImage = _monitorDBContex.TestImages.FirstOrDefault(im => im.HiveId == id);
                if (crtHiveImage == null)
                {
                    crtHiveImage = ModelFactoryTestImage.Default;
                    ViewBag.localPath = LOCAL_PATH;

                }
                ViewBag.FileStatus = "File uploaded successfully.";
                return View("LoadImage", crtHiveImage);
            }
            else ViewBag.FileStatus = id + " is null. Default image loaded.";

            return View("Index");
        }

        [System.Web.Mvc.HttpPost]
        public ActionResult LoadImage(string id, HttpPostedFileBase theFile)
        {
            //POST (load) an image from hard disk  
            //method is called when button LoadAnImagefromDisk is pressed
            // OR POST (load) the image received from Android
            if (Request.Files.Count > 0)
            {
                try
                {
                    HttpPostedFileBase postedImage = Request.Files[0];  // Request.Files["File"];
                    if (postedImage != null && postedImage.ContentLength>0)
                    {
                        string deployPath = Server.MapPath(LOCAL_PATH);
                        string destinationPath = deployPath + "\\" + id;
                        DirectoryInfo di = Directory.CreateDirectory(destinationPath);
                        string path = Path.Combine(destinationPath, Path.GetFileName(postedImage.FileName));
                      
                        postedImage.SaveAs(path);
                      
                        TestImage modelToSendToView = _monitorDBContex.TestImages.FirstOrDefault(im => im.HiveId == id);
                        if (modelToSendToView == null)
                        {
                            modelToSendToView = _monitorDBContex.TestImages.Create();
                            _monitorDBContex.TestImages.Add(modelToSendToView);
                        }

                        modelToSendToView.HiveId = id;
                        modelToSendToView.ImageName = postedImage.FileName;
                        modelToSendToView.FileCRC = getCRCHashCodeforFile(modelToSendToView, path);
                        _monitorDBContex.SaveChanges();

                        ViewBag.localPath = LOCAL_PATH + "/" + id;
                        ViewBag.FileStatus = "File upload - OK"; // this message is sent back to Android
                        return View("LoadImage", modelToSendToView);
                    }
                }
                catch (Exception e)
                {
                    string err = e.ToString();

                    ViewBag.FileStatus = "Error while file uploading.";
                }
            }

           
            ViewBag.FileStatus = "File uploaded failed.";
            return View();
        }

       
        private int getCRCHashCodeforFile(TestImage crtHiveImage, string filePath)
        {
          
            String hash = String.Empty;
            FileStream imageFile = new FileStream(filePath, FileMode.Open, FileAccess.Read);
            int imageFileHashValue = imageFile.GetHashCode();
            imageFile.Close();

            return imageFileHashValue;
        }  

    }
}