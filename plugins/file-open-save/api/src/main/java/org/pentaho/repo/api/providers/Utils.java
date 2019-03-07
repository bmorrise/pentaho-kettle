/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2019 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.repo.api.providers;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bmorrise on 2/23/19.
 */
public class Utils {

  public static List<String>
    validExtensions = Arrays
    .asList( "dsn", "ph", "config", "aze", "rom", "bowerrc", "ktr", "ini", "kts", "p7b", "docm", "rtf", "rub", "ico",
      "vmdk", "pyc", "wsf", "deb", "cal", "ra", "tcsh", "txt", "kt", "xcf", "coffeelintignore", "3ga", "eot",
      "handlebars", "pub", "rar", "bpg", "docx", "img", "pom", "ott", "rb", "ru", "xml", "ace", "gitattributes", "js",
      "j2", "ibooks", "vox", "jsp", "3g2", "py", "sass", "java", "raw", "codekit", "webm", "kup", "master", "enc",
      "otf", "cptx", "plist", "po", "step", "bash", "xlm", "sql", "ocx", "ttf", "crdownload", "el", "map", "3gp", "pm",
      "dgn", "tar", "lex", "tiff", "nix", "conf", "lisp", "eml", "tmp", "pl", "blank", "scss", "in", "compile", "cpp",
      "mpeg", "au", "f4v", "dot", "vss", "doc", "dng", "vsd", "zip", "lock", "pgp", "bz2", "wav", "yml", "gdp", "rsa",
      "rpm", "json", "key", "pst", "asmx", "vscodeignore", "ash", "ac", "zsh", "aif", "csh", "m4", "msg", "accdt", "aa",
      "dll", "cer", "mkv", "xps", "tfignore", "applescript", "flac", "wmv", "rst", "wma", "pkg", "xaml", "download",
      "pem", "m3u8", "licx", "kjb", "resx", "tga", "dbf", "jar", "asax", "sol", "retry", "app", "accdb", "m", "vdx",
      "csproj", "fb2", "z", "mc", "ogv", "dwg", "xfl", "tpl", "fnt", "msu", "asx", "psd", "idx", "coffee", "wbk", "iff",
      "gif", "bzempty", "cfm", "cgi", "xsl", "indd", "dist", "ts", "vst", "class", "sys", "h", "tsv", "md", "ds_store",
      "hlp", "fax", "ost", "phar", "ps1", "psp", "lnk", "rdl", "bak", "eps", "lua", "war", "p12", "rss", "wmf", "heic",
      "gpg", "info", "gadget", "tex", "asm", "vdi", "ogg", "ait", "me", "jpeg", "mpa", "pages", "vb", "htm", "icns",
      "pdb", "ppsx", "cfml", "woff2", "pid", "sldm", "image", "7z", "midi", "mi", "stl", "xlsm", "mk", "csv", "xlsx",
      "mp4", "asc", "ascx", "bmp", "pps", "sldx", "dart", "reg", "aifc", "sqlite", "ksh", "xspf", "default", "dat",
      "vsx", "mdf", "m2v", "mpt", "part", "ai", "svg", "ods", "cr2", "fon", "swp", "css", "mpp", "mpg", "asf", "mdb",
      "xsd", "gpl", "rdf", "iso", "rpt", "php", "gem", "aiff", "code-workspace", "ifo", "webinfo", "mid", "tgz", "xpi",
      "asp", "apk", "tif", "adn", "cs", "cd", "mo", "mpga", "crypt", "swf", "swd", "fla", "odt", "flv", "mm", "crt",
      "dxf", "mp2", "c", "mpd", "browser", "dmg", "m3u", "chm", "sln", "editorconfig", "epub", "pfx", "twig", "pdf",
      "eslintignore", "bat", "log", "cson", "kf8", "ppt", "xrb", "cfg", "woff", "xz", "mpe", "vbproj", "mp3", "msi",
      "odb", "hbs", "dtd", "ani", "amr", "xlt", "sitemap", "yaml", "cad", "axd", "skin", "iml", "pcd", "inv", "dotm",
      "bin", "mov", "ova", "m4a", "m4v", "aspx", "jpe", "catalog", "dpj", "hs", "jpg", "vbs", "cmd", "mng", "sed",
      "dotx", "elf", "less", "udf", "lit", "ics", "swift", "inc", "nfo", "data", "ram", "gitignore", "cdda", "caf",
      "html", "com", "vob", "torrent", "potx", "xltm", "db", "avi", "qt", "hsl", "cab", "tax", "ovf", "wps",
      "npmignore", "pptx", "prop", "inf", "docb", "nef", "ashx", "sit", "jsx", "go", "vcs", "xls", "vcd", "aac", "gz",
      "vcf", "tmx", "vtx", "m4r", "sh", "ps", "sdf", "folder", "sphinx", "kmk", "webp", "nes", "png", "pot", "exe",
      "mobi", "pptm", "gradle", "mod", "aup", "rm", "diz", "cur", "xltx", "cue" );

  public static boolean matches( String name, String filters ) {
    return filters == null || name.matches( filters.replace( ".", ".*\\." ) );
  }

  public static String getExtension( String path ) {
    return path.substring( path.lastIndexOf( "." ) + 1, path.length() );
  }

  public static boolean isValidExtension( String extension ) {
    return validExtensions.contains( extension );
  }

  public static String getParent( String path ) {
    return path.substring( 0, path.lastIndexOf( "/" ) );
  }

  public static String getName( String path ) {
    return path.substring( path.lastIndexOf( "/" ), path.length() );
  }
}
