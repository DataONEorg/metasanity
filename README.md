# README for Metasanity

No-frills schema aware metadata validator.

Populate the schema repository with:

```
rsync -avz -e "ssh" cn.dataone.org:/var/lib/tomcat7/webapps/metacat/schema schema
```

Generate the schemsa.json file with:

```
select array_to_json(array_agg(t)) from (SELECT public_id, system_id, format_id FROM xml_catalog where entry_type='Schema') t;
```
