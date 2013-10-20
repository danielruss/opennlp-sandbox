/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package opennlp.modelbuilder.v2.impls;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import opennlp.modelbuilder.v2.ModelGenerationValidator;

/**
 *
 * @author Owner
 */
public class FileModelValidatorImpl implements ModelGenerationValidator {

  private Set<String> badentities = new HashSet<String>();
  private final double MIN_SCORE_FOR_TRAINING = 0.95d;
  private Object validationData;
  private Map<String, String> params = new HashMap<String, String>();

  @Override
  public void setParameters(Map<String, String> params) {
    this.params = params;
  }

  @Override
  public Boolean validSentence(String sentence) {
    //returning true by default, because the sentence provider will  return only "valid" sentences in this case
    return true;
  }

  @Override
  public Boolean validNamedEntity(String namedEntity) {

    if (badentities.isEmpty()) {
      getBlackList();
    }

    Pattern p = Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    if (p.matcher(namedEntity).find()) {
      return false;
    }
    Boolean b = true;
    if (badentities.contains(namedEntity.toLowerCase())) {
      b = false;
    }
    return b;
  }

  @Override
  public Collection<String> getBlackList() {
    if (!badentities.isEmpty()) {
      try {
        InputStream fis;
        BufferedReader br;
        String line;

        fis = new FileInputStream(params.get("blacklistfile"));
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        while ((line = br.readLine()) != null) {
          badentities.add(line);
        }        
        br.close();
        br = null;
        fis = null;
      } catch (FileNotFoundException ex) {
        Logger.getLogger(FileKnownEntityProvider.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(FileKnownEntityProvider.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return badentities;
  }
}