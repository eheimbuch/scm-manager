/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.scm.repository;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.ConfigurationException;
import sonia.scm.io.CommandResult;
import sonia.scm.io.ExtendedCommand;
import sonia.scm.io.INIConfiguration;
import sonia.scm.io.INIConfigurationReader;
import sonia.scm.io.INIConfigurationWriter;
import sonia.scm.io.INISection;
import sonia.scm.util.IOUtil;
import sonia.scm.util.Util;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sebastian Sdorra
 */
public class HgRepositoryHandler extends AbstractRepositoryHandler<HgConfig>
{

  /** Field description */
  public static final String TYPE_DISPLAYNAME = "Mercurial";

  /** Field description */
  public static final String TYPE_NAME = "hg";

  /** Field description */
  public static final RepositoryType TYPE = new RepositoryType(TYPE_NAME,
                                              TYPE_DISPLAYNAME);

  /** Field description */
  private static final Logger logger =
    Logger.getLogger(HgRepositoryHandler.class.getName());

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @throws IOException
   * @throws RepositoryException
   */
  @Override
  public void create(Repository repository)
          throws RepositoryException, IOException
  {
    initNewRepository(repository);

    File directory = getDirectory(repository);

    if (directory.exists())
    {
      throw new RepositoryAllreadyExistExeption();
    }

    ExtendedCommand command = new ExtendedCommand(config.getHgBinary(), "init",
                                directory.getPath());
    CommandResult result = command.execute();

    if (!result.isSuccessfull())
    {
      StringBuilder msg = new StringBuilder("hg exit with error ");

      msg.append(result.getReturnCode()).append(" and message: '");
      msg.append(result.getOutput()).append("'");

      throw new RepositoryException(msg.toString());
    }

    repository.setType(TYPE_NAME);

    File hgDirectory = new File(directory, ".hg");

    storeRepository(repository, hgDirectory);
  }

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @throws IOException
   * @throws RepositoryException
   */
  @Override
  public void delete(Repository repository)
          throws RepositoryException, IOException
  {
    File directory = getDirectory(repository);

    if (new File(directory, ".hg").exists())
    {
      IOUtil.delete(directory);
    }
    else
    {
      throw new RepositoryException("repository does not exists");
    }
  }

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @throws IOException
   * @throws RepositoryException
   */
  @Override
  public void modify(Repository repository)
          throws RepositoryException, IOException
  {
    File directory = getDirectory(repository);
    File hgDirectory = new File(directory, ".hg");

    if (hgDirectory.exists())
    {
      storeRepository(repository, hgDirectory);
    }
  }

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @throws IOException
   * @throws RepositoryException
   */
  @Override
  public void refresh(Repository repository)
          throws RepositoryException, IOException
  {
    File directory = getDirectory(repository);
    File hgDirectory = new File(directory, ".hg");

    if (hgDirectory.exists())
    {
      loadRepository(repository, hgDirectory);
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param id
   *
   * @return
   */
  @Override
  public Repository get(String id)
  {
    Repository repository = null;
    File[] directories = getRepositoryDirectory().listFiles();

    for (File directory : directories)
    {
      File hgDirectory = new File(directory, ".hg");

      if (hgDirectory.exists() && hgDirectory.isDirectory())
      {
        repository = new Repository();
        repository.setType(TYPE_NAME);
        repository.setName(directory.getName());
        loadRepository(repository, hgDirectory);

        if (!id.equals(repository.getId()))
        {
          repository = null;
        }
        else
        {
          break;
        }
      }
    }

    return repository;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public Collection<Repository> getAll()
  {
    List<Repository> repositories = new ArrayList<Repository>();
    String[] repositoryNames = getRepositoryDirectory().list();

    if ((repositoryNames != null) && (repositoryNames.length > 0))
    {
      for (String repositoryName : repositoryNames)
      {
        Repository repository = buildRepository(repositoryName);

        if (repository != null)
        {
          if (Util.isNotEmpty(repository.getId()))
          {
            repositories.add(repository);
          }
        }
      }
    }

    return repositories;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public RepositoryType getType()
  {
    return TYPE;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  protected Class<HgConfig> getConfigClass()
  {
    return HgConfig.class;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param section
   * @param permissions
   */
  private void appendPermission(INISection section,
                                Collection<Permission> permissions)
  {
    HgPermissionBuilder builder = new HgPermissionBuilder(permissions);

    section.setParameter("allow_read", builder.getReadPermission());
    section.setParameter("allow_push", builder.getWritePermission());
  }

  /**
   * Method description
   *
   *
   * @param name
   *
   * @return
   */
  private Repository buildRepository(String name)
  {
    Repository repository = null;
    File directory = getDirectory(name);

    if (directory.exists() && directory.isDirectory())
    {
      File hgDirectory = new File(directory, ".hg");

      if (hgDirectory.exists() && hgDirectory.isDirectory())
      {
        repository = new Repository();
        repository.setType(TYPE_NAME);
        repository.setName(name);
        repository.setUrl(buildUrl(repository));
        loadRepository(repository, hgDirectory);
      }
    }

    return repository;
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param hgDirectory
   */
  private void loadRepository(Repository repository, File hgDirectory)
  {
    File hgrc = new File(hgDirectory, "hgrc");

    if (hgrc.exists() && hgrc.isFile())
    {
      try
      {
        INIConfiguration iniConfig = new INIConfigurationReader().read(hgrc);
        INISection section = iniConfig.getSection("web");

        if (section != null)
        {
          repository.setDescription(section.getParameter("description"));
          repository.setContact(section.getParameter("contact"));

          String read = section.getParameter("allow_read");
          String write = section.getParameter("allow_push");

          if ((read != null) || (write != null))
          {
            if (read == null)
            {
              read = "";
            }

            if (write == null)
            {
              write = "";
            }

            List<Permission> permissions =
              new HgPermissionReader().readPermissions(read, write);

            repository.setPermissions(permissions);
          }
        }

        INISection scmSection = iniConfig.getSection("scm");

        if (scmSection != null)
        {
          String id = scmSection.getParameter("id");

          repository.setId(id);

          String creationDateString = scmSection.getParameter("creationDate");

          if (Util.isNotEmpty(creationDateString))
          {
            try
            {
              repository.setCreationDate(Util.parseDate(creationDateString));
            }
            catch (ParseException ex)
            {
              logger.log(Level.SEVERE, null, ex);
            }
          }
        }
      }
      catch (IOException ex)
      {
        logger.log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param hgDirectory
   *
   * @throws IOException
   */
  private void storeRepository(Repository repository, File hgDirectory)
          throws IOException
  {
    INISection section = new INISection("web");

    section.setParameter("name", repository.getName());

    String description = repository.getDescription();

    if (Util.isNotEmpty(description))
    {
      section.setParameter("description", description);
    }

    String contact = repository.getContact();

    if (Util.isNotEmpty(contact))
    {
      section.setParameter("contact", contact);
    }

    Collection<Permission> permissions = repository.getPermissions();

    if (permissions != null)
    {
      appendPermission(section, permissions);
    }

    INISection scmSection = new INISection("scm");

    scmSection.setParameter("id", repository.getId());

    Date creationDate = repository.getCreationDate();

    if (creationDate != null)
    {
      scmSection.setParameter("creationDate", Util.formatDate(creationDate));
    }

    INIConfiguration iniConfig = new INIConfiguration();

    iniConfig.addSection(section);
    iniConfig.addSection(scmSection);

    File hgrc = new File(hgDirectory, "hgrc");
    INIConfigurationWriter writer = new INIConfigurationWriter();

    writer.write(iniConfig, hgrc);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param id
   *
   * @return
   */
  private File getDirectory(String id)
  {
    return new File(config.getRepositoryDirectory(), id);
  }

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   */
  private File getDirectory(Repository repository)
  {
    return new File(config.getRepositoryDirectory(), repository.getName());
  }

  /**
   * Method description
   *
   *
   * @return
   */
  private File getRepositoryDirectory()
  {
    File directory = null;

    if (isConfigured())
    {
      directory = config.getRepositoryDirectory();

      if (!directory.exists() &&!directory.mkdirs())
      {
        throw new ConfigurationException(
            "could not create directory ".concat(directory.getPath()));
      }
    }

    return directory;
  }
}
