prefix = function(){
    return db.phones.aggregate([{$group: {_id:"$components.prefix", numPhones: {$sum: 1}}}]);
}

prefix().forEach(printjson);

//output
/*
{
  _id: 233,
  numPhones: 33471
}
{
  _id: 231,
  numPhones: 33295
}
{
  _id: 21,
  numPhones: 33432
}
{
  _id: 234,
  numPhones: 33317
}
{
  _id: 232,
  numPhones: 33166
}
{
  _id: 22,
  numPhones: 33319
}

 */