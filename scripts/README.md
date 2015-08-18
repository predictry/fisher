# Items Synchronization

`item_similiar_add.pl` will listen on new recommendation for an item and copy the recommendation Json file into 
`/volumes/neo/items` folder.  The content of this folder will replicate the structure of AWS S3 storage for similiar
item recommendations.

`item_delete.pl` will remove recommended item in Json files and also delete the Json recommendation file for the 
deleted item in `/volumes/neo/items`.  After that, it will perform sync into S3.